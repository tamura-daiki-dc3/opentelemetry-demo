package main

import (
	"context"
	"encoding/json"

	log "github.com/sirupsen/logrus"

	"github.com/IBM/sarama"
)

var (
	saramaConfig *sarama.Config
)

func init() {
}

func StartConsumerGroup(ctx context.Context) error {
	saramaConfig = sarama.NewConfig()
	saramaConfig.Version = sarama.V3_0_0_0
	saramaConfig.Producer.Return.Errors = true
	saramaConfig.Producer.Return.Successes = true
	saramaConfig.Producer.Retry.Max = 3
	saramaConfig.Consumer.Interceptors = []sarama.ConsumerInterceptor{NewOTelInterceptor("golang-kafka-consumer")}

	consumerGroup, err := sarama.NewConsumerGroup([]string{"kafka:9092"}, "golang-kafka-consumer", saramaConfig)
	if err != nil {
		return err
	}

	consumer := Consumer{
		ready: make(chan bool),
	}

	err = consumerGroup.Consume(ctx, []string{"test.A"}, &consumer)
	if err != nil {
		return err
	}
	return nil

}

// ConsumedMessage 受信メッセージ
type ConsumedMessage struct {
	Message   string `json:"message"`
	Timestamp int64  `json:"timestamp"`
}

// Consumer represents a Sarama consumer group consumer
type Consumer struct {
	ready chan bool
}

// Setup is run at the beginning of a new session, before ConsumeClaim
func (consumer *Consumer) Setup(_ sarama.ConsumerGroupSession) error {
	// Mark the consumer as ready
	close(consumer.ready)
	return nil
}

// Cleanup is run at the end of a session, once all ConsumeClaim goroutines have exited
func (consumer *Consumer) Cleanup(_ sarama.ConsumerGroupSession) error {
	return nil
}

// ConsumeClaim must start a consumer loop of ConsumerGroupClaim's Messages().
// Once the Messages() channel is closed, the Handler must finish its processing
// loop and exit.
func (consumer *Consumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	// NOTE:
	// Do not move the code below to a goroutine.
	// The `ConsumeClaim` itself is called within a goroutine, see:
	// https://github.com/IBM/sarama/blob/main/consumer_group.go#L27-L29

	for {
		select {
		case message, ok := <-claim.Messages():
			if !ok {
				log.Printf("message channel was closed")
				return nil
			}
			var consumed ConsumedMessage
			if err := json.Unmarshal(message.Value, &consumed); err != nil {
				log.Errorln(err)
			}
			log.Infof("Message claimed: value = %s, timestamp = %v, topic = %s", string(message.Value), message.Timestamp, message.Topic)
			session.MarkMessage(message, "")

		// Should return when `session.Context()` is done.
		// If not, will raise `ErrRebalanceInProgress` or `read tcp <ip>:<port>: i/o timeout` when kafka rebalance. see:
		// https://github.com/IBM/sarama/issues/1192
		case <-session.Context().Done():
			return nil
		}
	}
}
