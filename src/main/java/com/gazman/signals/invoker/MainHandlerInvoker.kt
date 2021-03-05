package com.gazman.signals.invoker

import android.os.Handler
import android.os.Looper

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
class MainHandlerInvoker : HandlerInvoker() {
    init {
        setHandler(Handler(Looper.getMainLooper()))
    }
}