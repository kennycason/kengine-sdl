package com.kengine.log

import com.kengine.hooks.context.Context

class LoggerContext private constructor(
    var logLevel: Logger.Level = Logger.Level.INFO
) : Context() {

    init {
        Logger.setLevel(logLevel)
    }

    companion object {
        private var currentContext: LoggerContext? = null

        fun create(
            logLevel: Logger.Level = Logger.Level.INFO
        ): LoggerContext {
            if (currentContext != null) {
                throw IllegalStateException("SDLContext has already been created. Call cleanup() before creating a new context.")
            }
            currentContext = LoggerContext(
                logLevel = logLevel
            )
            return currentContext!!
        }

        fun get(): LoggerContext {
            return currentContext ?: throw IllegalStateException("LoggerContext has not been created. Call create() first.")
        }

    }

    override fun cleanup() {
    }

}

