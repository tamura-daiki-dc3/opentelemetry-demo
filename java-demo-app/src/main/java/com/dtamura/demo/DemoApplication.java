package com.dtamura.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

// TODO (Trace) import文を追加
import org.springframework.context.annotation.Bean;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
// TODO (Metrics) import文を追加

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// TODO (Trace) OpenTelemetry初期化コードの追加
	@Bean
	public OpenTelemetry openTelemetry() {

		// アプリケーションに関する属性情報をタグ付けする
		Resource resource = Resource.getDefault().toBuilder()
				.put(ResourceAttributes.SERVICE_NAME, DemoApplication.class.getName())
				.put(ResourceAttributes.SERVICE_VERSION, "0.1.0").build();

		// OpenTelemetry CollectorにgRPCで送信するためのエクスポーター
		OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder().setEndpoint("http://otel-collector:4317")
				.build();

		// トレーサーを生成するTracerProviderを設定する。上記のエクスポーターを登録
		SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
				.addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build()) // Collectorに送る
				.setResource(resource)
				.build();

		// TODO (Metrics) メトリクスの設定

		// TODO (Metrics) OpenTelemetrySdkへ登録
		OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
				.setTracerProvider(sdkTracerProvider)
				.setPropagators(ContextPropagators.create(TextMapPropagator
						.composite(W3CTraceContextPropagator.getInstance(), W3CBaggagePropagator.getInstance())))
				.build();

		return openTelemetry;
	}

	// ここまで

}
