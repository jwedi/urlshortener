package com.johanwedin.urlshortener.helpers.tracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import java.time.Duration;

public class TracingConfiguration {
    /** The number of milliseconds between metric exports. */
    private static final long METRIC_EXPORT_INTERVAL_MS = 800L;

    /**
     * Initializes an OpenTelemetry SDK with a logging exporter and a SimpleSpanProcessor.
     *
     * @return A ready-to-use {@link OpenTelemetry} instance.
     */
    public static OpenTelemetry initOpenTelemetry() {
        MetricReader periodicReader =
                PeriodicMetricReader.builder(LoggingMetricExporter.create())
                        .setInterval(Duration.ofMillis(METRIC_EXPORT_INTERVAL_MS))
                        .build();

        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder().registerMetricReader(periodicReader).build();

        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                        .build();
        return OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }
}
