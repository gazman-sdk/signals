package com.gazman.signals.invoker

import android.os.Handler
import java.lang.reflect.Method

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
open class HandlerInvoker : DefaultInvoker() {
    private var handler: Handler? = null
    private var runInstantlyIfOnHandlerThread = true
    fun setRunInstantlyIfOnHandlerThread(runInstantlyIfOnHandlerThread: Boolean) {
        this.runInstantlyIfOnHandlerThread = runInstantlyIfOnHandlerThread
    }

    fun setHandler(handler: Handler?) {
        this.handler = handler
    }

    override fun invoke(method: Method?, args: Array<Any?>?, listener: Any) {
        if (runInstantlyIfOnHandlerThread) {
            if (Thread.currentThread() === handler!!.looper.thread) {
                super.invoke(method, args, listener)
                return
            }
        }
        handler!!.post(BaseInvoker(method, args, listener))
    }
}