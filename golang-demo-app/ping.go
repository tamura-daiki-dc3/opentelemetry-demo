package main

import (
	"context"
	"encoding/json"
	"net/http"
	"os"

	log "github.com/sirupsen/logrus"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/trace"
)

func pingHandler(w http.ResponseWriter, r *http.Request) {

	span := trace.SpanFromContext(r.Context())

	email := r.Header.Get("X-Goog-Authenticated-User-Email")
	if email != "" {
		span.SetAttributes((attribute.String("X-Goog-Authenticated-User-Email", email)))
	}

	msg := ping(r.Context())
	log.WithFields(commonLogFieleds(span)).Info(msg)
	span.SetAttributes(attribute.String("pong", msg))

	// Response
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{"msg": msg})
}

func ping(ctx context.Context) string {
	ctx, span := tracer.Start(ctx, "pong", trace.WithSpanKind(trace.SpanKindClient))
	defer span.End()

	// create http request
	client := &http.Client{}

	target := os.Getenv("PING_TARGET_URL")
	req, err := http.NewRequest("GET", target+"/greeting", nil)
	if err != nil {
		log.Error(err)
		return ""
	}
	propagator := otel.GetTextMapPropagator()
	propagator.Inject(ctx, propagation.HeaderCarrier(req.Header))

	if err != nil {
		log.Warn(err)
	}
	req.Header.Add("Content-Type", "application/json")

	// Start Request
	resp, err := client.Do(req)
	if err != nil {
		log.Error(err)
		span.End()
		return ""
	}
	defer resp.Body.Close()

	var data map[string]string
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		log.Error(err)
		span.RecordError(err)
		span.End()
		return ""
	}
	span.End()

	return "ping"
}
