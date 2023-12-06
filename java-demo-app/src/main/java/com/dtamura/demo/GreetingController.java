package com.dtamura.demo;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO import文を追加ここから
// ここまで

@RestController
public class GreetingController {

   private static final String template = "Hello, %s!";
   private final AtomicLong counter = new AtomicLong();

   private Logger logger = LogManager.getLogger(GreetingController.class.getName());

   // TODO Tracerプライベート変数を追加

   // TODO コンストラクタの引数にOpenTelemetryを追加
   // TODO コンストラクタ内でtracer変数を初期化
   public GreetingController() {
   }

   @GetMapping("/greeting")
   public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

      // TODO スパンの開始コードを追加

      logger.info("start greeting");

      hoge();
      // TODO スパンの終了コードを追加

      logger.info("end greeting");
      return new Greeting(counter.incrementAndGet(), String.format(template, name));
   }

   public void hoge() {
      // TODO スパンの開始コードを追加
      logger.info("hoge");
      // TODO スパンの終了コードを追加
   }
}
