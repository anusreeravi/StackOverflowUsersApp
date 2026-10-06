package com.candyspace.stackoverflowusers.modules

import com.candyspace.stackoverflowusers.telemetry.LogcatTelemetryLogger
import com.candyspace.stackoverflowusers.telemetry.TelemetryLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TelemetryModule {

    @Binds
    @Singleton
    abstract fun bindTelemetryLogger(
        logger: LogcatTelemetryLogger,
    ): TelemetryLogger
}
