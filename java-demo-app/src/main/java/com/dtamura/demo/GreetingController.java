package com.dtamura.demo;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO (Trace) import文を追加
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.util.ArrayList;
import java.util.List;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
// TODO (Metrics) import文を追加

@RestController
public class GreetingController {

   private static final String template = "Hello, %s!";
   private final AtomicLong counter = new AtomicLong();

   private final Logger logger = LogManager.getLogger(GreetingController.class.getName());

   // TODO (Trace) Tracerプライベート変数を追加
   private final Tracer tracer;
   private final OpenTelemetry openTelemetry;
   // TODO (Metrics) Meter, LongCounterプライベート変数を追加

   // HTTPヘッダから、トレースID等の情報を抽出するために使われるgetter関数
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

   // TODO (Trace) コンストラクタの引数にOpenTelemetryを追加
   // TODO (Trace) コンストラクタ内でtracer変数を初期化
   // TODO (Metrics) コンストラクタ内でmetrics変数を初期化
   public GreetingController(OpenTelemetry openTelemetry) {
      this.openTelemetry = openTelemetry;
      this.tracer = openTelemetry.getTracer(GreetingController.class.getName(), "0.1.0");
   }

   @GetMapping("/greeting")
   public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name,
         @RequestHeader HttpHeaders headers) {

      // TODO (Trace) スパンの開始コードを追加
      Context extractedContext = this.openTelemetry.getPropagators().getTextMapPropagator()
            .extract(Context.current(), headers, this.getter);
      Span span = tracer.spanBuilder("greeting").setParent(extractedContext).startSpan();

      logger.info("start greeting");

      try (Scope scope = span.makeCurrent()) {
         hoge();
      } finally {
         // TODO (Trace) スパンの終了コードを追加
         span.end();
      }

      logger.info("end greeting");

      // TODO (Metrics) カウンターを1ずつ増加するコードを追加

      return new Greeting(counter.incrementAndGet(), String.format(template, name));
   }

   public void hoge() {
      // TODO (Trace) スパンの開始コードを追加
      Span span = tracer.spanBuilder("hoge").startSpan();

      logger.info("hoge");
      // TODO (Trace) スパンの終了コードを追加
      span.end();
   }
}
