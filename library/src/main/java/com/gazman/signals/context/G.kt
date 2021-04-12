package com.gazman.signals.context

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A ContextWrapper for application context
 */
@SuppressLint("StaticFieldLeak")
object G {

    @JvmField
    val app: Context = AppContext()

    @JvmField
    val main = Handler(Looper.getMainLooper())

    /**
     * Cached Thread Pool
     */
    @JvmField
    val CE: ExecutorService = Executors.newCachedThreadPool()

}