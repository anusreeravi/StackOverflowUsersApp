package com.candyspace.stackoverflowusers.telemetry

class FakeTelemetryLogger : TelemetryLogger {
    val loggedEvents = mutableListOf<Pair<String, Map<String, String>>>()
    val loggedErrors = mutableListOf<Triple<String, String, Throwable?>>()

    override fun logEvent(event: String, params: Map<String, String>) {
        loggedEvents.add(event to params)
    }

    override fun logError(scenario: String, message: String, throwable: Throwable?) {
        loggedErrors.add(Triple(scenario, message, throwable))
    }

    fun hasEvent(eventName: String): Boolean {
        return loggedEvents.any { it.first == eventName }
    }

    fun hasError(scenario: String): Boolean {
        return loggedErrors.any { it.first == scenario }
    }
}
