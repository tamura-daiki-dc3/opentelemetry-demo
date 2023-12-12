package com.dtamura.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

// TODO (Trace) import文を追加

// TODO (Metrics) import文を追加

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// TODO (Trace) OpenTelemetry初期化コードの追加

	// TODO (Metrics) メトリクスの設定

	// TODO (Metrics) OpenTelemetrySdkへ登録

}
