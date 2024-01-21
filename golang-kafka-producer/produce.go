package main

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strconv"
	"time"

	log "github.com/sirupsen/logrus"

	"github.com/IBM/sarama"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/codes"
	"go.opentelemetry.io/otel/propagation"
	semconv "go.opentelemetry.io/otel/semconv/v1.17.0"
	"go.opentelemetry.io/otel/trace"
)

var (
	saramaConfig   *sarama.Config
	saramaProducer sarama.AsyncProducer
)

func init() {
	saramaConfig = sarama.NewConfig()
	saramaConfig.Version = sarama.V3_0_0_0
	saramaConfig.Producer.Return.Errors = true
	saramaConfig.Producer.Return.Successes = true
	saramaConfig.Producer.Retry.Max = 3

	var err error
	saramaProducer, err = sarama.NewAsyncProducer([]string{"kafka:9092"}, saramaConfig)
	if err != nil {
		log.Fatalln(err)
		os.Exit(1)
	}
}

// SendMessage 送信メッセージ
type SendMessage struct {
	Message   string `json:"message"`
	Timestamp int64  `json:"timestamp"`
}

func produceHandler(w http.ResponseWriter, r *http.Request) {

	msg := buildMsg(r.Context())
	err := publish(r.Context(), msg)

	if err != nil {
		// Response
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(map[string]string{"msg": err.Error()})
		return
	}

	// Response
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{"msg": "ok"})

}

func buildMsg(ctx context.Context) *sarama.ProducerMessage {
	_, span := tracer.Start(ctx, "buildMsg")
	defer span.End()

	ts := time.Now().UnixNano()
	msg := &SendMessage{
		Message:   "Hello",
		Timestamp: ts,
	}
	bytes, err := json.Marshal(msg)
	if err != nil {
		panic(err)
	}
	return &sarama.ProducerMessage{
		Topic: "test.A",
		Key:   sarama.StringEncoder(strconv.FormatInt(ts, 10)),
		Value: sarama.StringEncoder(string(bytes)),
	}
}

func publish(ctx context.Context, msg *sarama.ProducerMessage) error {

	ctx, span := tracer.Start(ctx, fmt.Sprintf("%s publish", msg.Topic),
		trace.WithSpanKind(trace.SpanKindProducer),
		trace.WithAttributes(
			semconv.PeerService("kafka"),
			semconv.NetTransportTCP,
			semconv.MessagingSystem("kafka"),
			semconv.MessagingDestinationKindTopic,
			semconv.MessagingDestinationName(msg.Topic),
			semconv.MessagingOperationPublish,
			semconv.MessagingKafkaDestinationPartition(int(msg.Partition)),
		))
	defer span.End()

	carrier := propagation.MapCarrier{}
	propagator := otel.GetTextMapPropagator()
	propagator.Inject(ctx, carrier)

	for key, value := range carrier {
		msg.Headers = append(msg.Headers, sarama.RecordHeader{Key: []byte(key), Value: []byte(value)})
	}

	// 送信
	saramaProducer.Input() <- msg
	select {
	case <-saramaProducer.Successes():
		log.Printf("sucess send message: %v, timestamp: %v\n", msg.Value, msg.Timestamp)
		span.SetStatus(codes.Ok, "OK")
		return nil
	case err := <-saramaProducer.Errors():
		log.Errorf("fail send. err: %v", err)
		return err
	}
}
