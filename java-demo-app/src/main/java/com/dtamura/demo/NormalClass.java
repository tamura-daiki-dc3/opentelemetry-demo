package com.dtamura.demo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

public class NormalClass {

    private Tracer tracer;

    NormalClass(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(NormalClass.class.getName(), "0.1.0");
    }

    public void piyo() {
        Span span = tracer.spanBuilder("NormalClass.piyo").startSpan();
        long id = Thread.currentThread().getId();
        span.setAttribute("ThreadID", id);
        System.out.println("Thread ID: " + id);

        span.end();
    }
}
