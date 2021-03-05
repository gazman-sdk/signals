package com.gazman.signals

/**
 * A default error handler for all the invoked signals
 */
object UnhandledExceptionHandler {
    /**
     * An optional error handler callback for all the invoked signals
     */
    var callback: Callback? = null

    interface Callback {
        /**
         * A callback for a crash during signal invocation
         */
        fun onApplicationError(throwable: Throwable?)
    }
}