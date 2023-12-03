package com.dtamura.demo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

public class ThreadClass implements Runnable {

    private Tracer tracer;
    private final Span rootSpan;

    public ThreadClass(OpenTelemetry opentelemetry, Span rootSpan) {
        this.tracer = opentelemetry.getTracer(ThreadClass.class.getName(), "0.1.0");
        this.rootSpan = rootSpan;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Span span = tracer.spanBuilder("ThreadClass.run")
                .setParent(Context.current().with(rootSpan))
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            long id = Thread.currentThread().getId();
            span.setAttribute("ThreadID", id);
            System.out.println("Thread ID: " + id);
        } finally {
            span.end();
        }
    }

}
