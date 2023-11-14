package com.dtamura.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@RestController
public class GreetingController {

   private static final String template = "Hello, %s!";
   private final AtomicLong counter = new AtomicLong();

   private Tracer tracer;

   public GreetingController(OpenTelemetry openTelemetry) {
      this.tracer = openTelemetry.getTracer(GreetingController.class.getName(), "0.1.0");
   }

   @GetMapping("/greeting")
   public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

      Span span = tracer.spanBuilder("hello").startSpan();
      try (Scope scope = span.makeCurrent()) {
         // span
      } finally {
         span.end();
      }
      return new Greeting(counter.incrementAndGet(), String.format(template, name));
   }
}
