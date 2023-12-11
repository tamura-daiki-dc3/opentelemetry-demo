package com.dtamura.demo;

import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.context.propagation.TextMapGetter;

@RestController
public class GreetingController {

   private static final String template = "Hello, %s!";
   private final AtomicLong counter = new AtomicLong();

   private final Tracer tracer;
   private final Meter meter;
   private final LongCounter longCounter;
   private OpenTelemetry openTelemetry;

   private Logger logger = LogManager.getLogger(GreetingController.class.getName());
   private final TextMapGetter<HttpHeaders> getter = new TextMapGetter<HttpHeaders>() {
      @Override
      public String get(HttpHeaders headers, String s) {
         assert headers != null;
         List<String> list = headers.get(s);
         if (list != null) {
            return list.get(0);
         }
         return null;
      }

      @Override
      public Iterable<String> keys(HttpHeaders headers) {
         List<String> keys = new ArrayList<>();
         headers.forEach((k, v) -> {
            keys.add(k);
         });
         return keys;
      }
   };

   public GreetingController(OpenTelemetry openTelemetry) {
      this.openTelemetry = openTelemetry;
      this.tracer = openTelemetry.getTracer(GreetingController.class.getName(), "0.1.0");
      // メトリクスの設定
      this.meter = openTelemetry.getMeterProvider().get(GreetingController.class.getName());
      this.longCounter = meter.counterBuilder("greeting_requests")
                     .setDescription("Total number of greeting requests")
                     .setUnit("1")
                     .build();
   }

   @GetMapping("/greeting")
   public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name,
         @RequestHeader HttpHeaders headers) {

      logger.info("start greeting");

      Context extractedContext = this.openTelemetry.getPropagators().getTextMapPropagator()
            .extract(Context.current(), headers, this.getter);

      Span span = tracer.spanBuilder("greeting").setSpanKind(SpanKind.SERVER)
            .setParent(extractedContext).startSpan();
      try (Scope scope = span.makeCurrent()) {
         // span
         hoge();
         span.setAttribute(io.opentelemetry.semconv.SemanticAttributes.HTTP_RESPONSE_STATUS_CODE, 200);
         span.setAttribute(io.opentelemetry.semconv.SemanticAttributes.HTTP_REQUEST_METHOD, "GET");
      } finally {
         span.end();
      }

      // カウンターを増やす
      longCounter.add(1);

      logger.info("end greeting");
      return new Greeting(counter.incrementAndGet(), String.format(template, name));
   }

   public void hoge() {
      // logger.info("start hoge");
      Span span = tracer.spanBuilder("hoge").startSpan();
      try (Scope scope = span.makeCurrent()) {
         logger.info("hoge");
      } finally {
         span.end();
      }

      logger.info("end hoge");
   }
}