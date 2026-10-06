package com.candyspace.stackoverflowusers.telemetry

interface TelemetryLogger {
    fun logEvent(event: String, params: Map<String, String> = emptyMap())
    fun logError(scenario: String, message: String, throwable: Throwable? = null)
}
