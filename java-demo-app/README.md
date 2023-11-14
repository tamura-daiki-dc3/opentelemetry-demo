


これを参考に作成するものの、
一部記載通りにうまく行かない。
https://opentelemetry.io/docs/instrumentation/java/manual/



bom とは？


そもそもこのバージョンはない。
-> 1.30.1-alpha
しかし、これも非推奨になっていて、
```
implementation("io.opentelemetry:opentelemetry-semconv:1.31.0-alpha");
```

以下に修正：
```
    implementation("io.opentelemetry.semconv:opentelemetry-semconv:1.22.0-alpha");
```

https://github.com/open-telemetry/semantic-conventions-java