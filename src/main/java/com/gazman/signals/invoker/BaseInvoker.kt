package com.gazman.signals.invoker

import android.os.Process
import com.gazman.signals.UnhandledExceptionHandler
import java.lang.reflect.Method

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
class BaseInvoker : Runnable {
    var method: Method? = null
    var args: Array<Any?>?
    var listener: Any? = null

    constructor(){
        args = emptyArray()
    }
    constructor(method: Method?, args: Array<Any?>?, listener: Any) {
        this.method = method
        this.args = args
        this.listener = listener
    }

    override fun run() {
        try {
            if (!method!!.isAccessible) {
                method!!.isAccessible = true
            }
            if(args == null) {
                method!!.invoke(listener)
            }
            else{
                method!!.invoke(listener, *args as Array<Any?>)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            if (UnhandledExceptionHandler.callback == null) {
                System.err.println("Unhandled Exception, consider providing UnhandledExceptionHandler.callback")
                try {
                    Thread.sleep(10)
                    Process.killProcess(Process.myPid())
                } catch (ignore: InterruptedException) {
                }
            } else {
                UnhandledExceptionHandler.callback!!.onApplicationError(e)
            }
        }
    }
}