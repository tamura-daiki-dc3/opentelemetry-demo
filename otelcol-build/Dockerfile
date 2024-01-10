FROM golang:1.20 as builder
WORKDIR /build
COPY config.yaml .
RUN go install go.opentelemetry.io/collector/cmd/builder@v0.91.0
RUN CGO_ENABLED=0 builder --config=config.yaml --name=otelcol-custom --output-path=.

FROM gcr.io/distroless/base-debian11
WORKDIR /app
COPY --from=builder /build/otelcol-custom /app

EXPOSE 4317/tcp 4318/tcp

ENTRYPOINT [ "/app/otelcol-custom" ]