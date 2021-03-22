package com.gazman.signals

import android.os.Handler
import com.gazman.signals.invoker.DefaultInvoker
import com.gazman.signals.invoker.ExecuteInvoker
import com.gazman.signals.invoker.HandlerInvoker
import com.gazman.signals.invoker.Invoker
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.concurrent.Executor

/**
 * Created by Ilya Gazman on 2/24/2015.
 */
class Signal<T> internal constructor(@JvmField val originalType: Class<T>) {

    companion object {
        private val DEFAULT_INVOKER: Invoker = DefaultInvoker()
    }

    @JvmField
    val dispatcher: T
    private val listeners = ListenersList<T>()
    private val oneTimeListeners = ListenersList<T>()
    private var invoker = DEFAULT_INVOKER


    init {
        val invocationHandler = InvocationHandler { _, method: Method, args: Array<Any?>? ->
            this@Signal.invoke(method, args)
            null
        }
        @Suppress("UNCHECKED_CAST")
        dispatcher = Proxy.newProxyInstance(originalType.classLoader, arrayOf<Class<*>>(originalType), invocationHandler) as T
    }

    /**
     * Use this executor to execute all calls for listeners
     */
    fun setInvoker(executor: Executor) {
        this.invoker = ExecuteInvoker(executor)
    }

    /**
     * Use this handler to execute all calls for listeners
     */
    fun setInvoker(handler: Handler) {
        this.invoker = HandlerInvoker(handler)
    }

    /**
     * Remove the invoker
     */
    fun clearInvoker() {
        invoker = DEFAULT_INVOKER
    }

    /**
     * Register for this signal, until removeListener is called.
     *
     * @param listener listener to register
     */
    fun addListener(listener: T) {
        synchronized(this) {
            listeners.add(listener)
        }
    }

    /**
     * Same as add listener, only it will immediately unregister
     * after the first dispatch.
     * Note that it does not matter how many method this listener got in the interface,
     * it will unregister after the first one to be dispatched
     *
     * @param listener listener to register
     */
    fun addListenerOnce(listener: T) {
        synchronized(this) {
            oneTimeListeners.add(listener)
        }
    }

    /**
     * Remove listener that been added through addListener or addListenerOnce
     *
     * @param listener listener to unregister
     */
    fun removeListener(listener: T) {
        synchronized(this) {
            listeners.remove(listener)
            oneTimeListeners.remove(listener)
        }
    }

    private operator fun invoke(method: Method?, args: Array<Any?>?) {
        for (listener in listeners) {
            if (listener != null) {
                invoker.invoke(method, args, listener)
            }
        }
        for (listener in oneTimeListeners) {
            if (listener != null) {
                invoker.invoke(method, args, listener)
            }
        }

        oneTimeListeners.clear()
    }

    fun hasListeners(): Boolean {
        return listeners.isNotEmpty() || oneTimeListeners.isNotEmpty()
    }
}