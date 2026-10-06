package com.candyspace.stackoverflowusers.telemetry

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogcatTelemetryLogger @Inject constructor() : TelemetryLogger {

    override fun logEvent(event: String, params: Map<String, String>) {
        val paramString = if (params.isNotEmpty()) {
            PARAMS + params.entries.joinToString { "${it.key}=${it.value}" }
        } else {
            ""
        }
        Log.i(TAG, "$EVENT$event$paramString")
    }

    override fun logError(scenario: String, message: String, throwable: Throwable?) {
        Log.e(TAG, "$ERROR $scenario$MESSAGE$message", throwable)
    }

    companion object {
        private const val TAG = "Telemetry"
        private const val EVENT = "[EVENT]:"
        private const val ERROR = "[ERROR] Scenario:"
        private const val PARAMS = " | Params: "
        private const val MESSAGE = "| Message: "
    }
}
