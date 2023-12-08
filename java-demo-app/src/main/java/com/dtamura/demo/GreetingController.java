package com.dtamura.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

@RestController
public class GreetingController {

   private static final String template = "Hello, %s!";
   private final AtomicLong counter = new AtomicLong();

   private Tracer tracer;

   private Logger logger = LogManager.getLogger(GreetingController.class.getName());

   public GreetingController(OpenTelemetry openTelemetry) {
      this.tracer = openTelemetry.getTracer(GreetingController.class.getName(), "0.1.0");
   }

   @GetMapping("/greeting")
   public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
   //public Greeting greeting(@RequestHeader("X-Trace-Id") String parentTraceId, @RequestParam(value = "name", defaultValue = "World") String name) {

      logger.info("start greeting");

      //TEST
      String parentTraceId = "12345";

      SpanContext spanContext = SpanContext.createFromRemoteParent(parentTraceId, name, TraceFlags.getDefault(), TraceState.getDefault());
      Span span = tracer.spanBuilder("hello").setParent(Context.current().with(Span.wrap(spanContext))).startSpan();
      try (Scope scope = span.makeCurrent()) {
         // span
         hoge(parentTraceId, name);
      } finally {
         span.end();
      }
      
      logger.info("end greeting");
      return new Greeting(counter.incrementAndGet(), String.format(template, name));
   }

   public void hoge(String parentTraceId, String name) {
      // logger.info("start hoge");
      SpanContext spanContext = SpanContext.createFromRemoteParent(parentTraceId, name, TraceFlags.getDefault(), TraceState.getDefault());
      Span span = tracer.spanBuilder("hoge").setParent(Context.current().with(Span.wrap(spanContext))).startSpan();
      try (Scope scope = span.makeCurrent()) {
         logger.info("hoge");
      } finally {
         span.end();
      }

      logger.info("end hoge");
   }
}
