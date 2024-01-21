"use client";

import type { Faro } from "@grafana/faro-react";
import { getWebInstrumentations, ReactIntegration } from "@grafana/faro-react";
import { TracingInstrumentation } from "@grafana/faro-web-tracing";
import { initializeFaro, ErrorsInstrumentation, WebVitalsInstrumentation, SessionInstrumentation, ConsoleInstrumentation, LogLevel } from "@grafana/faro-web-sdk";

export function initFaro(): Faro {
  const faro = initializeFaro({
    url: "http://localhost:8027/collect",
    apiKey: "supersecret",
    app: {
      name: "frontend",
      version: "1.0.0",
    },

    batching: {
      enabled: false,
    },

    instrumentations: [
      new ErrorsInstrumentation(),
      new WebVitalsInstrumentation(),
      new ConsoleInstrumentation({
        disabledLevels: [LogLevel.TRACE, LogLevel.ERROR], // console.log will be captured
      }),
      new SessionInstrumentation(),

      // Mandatory, initialization of the tracing package
      new TracingInstrumentation({
        instrumentationOptions: {
          // Requests to these URLs will have tracing headers attached.
          propagateTraceHeaderCorsUrls: [new RegExp("http://localhost:8080.*"), "http://localhost:8080"],
        },
      }),

      new ReactIntegration({}),
    ],
  });

  return faro;
}
