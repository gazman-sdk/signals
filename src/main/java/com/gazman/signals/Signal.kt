package com.gazman.signals

import com.gazman.signals.invoker.DefaultInvoker
import com.gazman.signals.invoker.Invoker
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Ilya Gazman on 2/24/2015.
 */
class Signal<T : Any> internal constructor(originalType: KClass<T>) {
    val dispatcher: T
    private val synObject = Any()
    private val listeners = ArrayList<T>()
    private val oneTimeListeners = LinkedList<T>()
    private var hasListeners = false
    private var invoker = DEFAULT_INVOKER

    init {
        val invocationHandler = InvocationHandler { proxy: Any, method: Method, args: Array<Any?>? ->
            this@Signal.invoke(method, args)
            null
        }
        @Suppress("UNCHECKED_CAST")
        dispatcher = Proxy.newProxyInstance(originalType.java.classLoader, arrayOf<Class<*>>(originalType.java), invocationHandler) as T
    }

    fun setInvoker(invoker: Invoker) {
        this.invoker = invoker
    }

    /**
     * Register for this signal, until removeListener is called.
     *
     * @param listener listener to register
     */
    fun addListener(listener: T) {
        applyListener(listeners, listener)
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
        applyListener(oneTimeListeners, listener)
    }

    private fun <TYPE> applyListener(list: MutableList<TYPE>, listener: TYPE) {
        validateListener(listener)
        synchronized(synObject) {
            if (!list.contains(listener)) {
                hasListeners = true
                list.add(listener)
            }
        }
    }

    private fun validateListener(listener: Any?) {
        if (listener == null) {
            throw NullPointerException("Listener can't be null")
        }
    }

    /**
     * Remove listener that been added through addListener or addListenerOnce
     *
     * @param listener listener to unregister
     */
    fun removeListener(listener: T) {
        validateListener(listener)
        synchronized(synObject) {
            listeners.remove(listener)
            oneTimeListeners.remove(listener)
            updateHasListeners()
        }
    }

    private fun updateHasListeners() {
        synchronized(synObject) { hasListeners = listeners.size + oneTimeListeners.size > 0 }
    }

    operator fun invoke(method: Method?, args: Array<Any?>?) {
        var listener: T?
        if (listeners.size > 0) {
            var i = 0
            while (true) {
                synchronized(synObject) {
                    listener = if (i < listeners.size) {
                        listeners[i]
                    } else {
                        null;
                    }
                }
                if (listener == null) {
                    break
                }
                invoker.invoke(method, args, listener!!)
                i++
            }
        }
        while (oneTimeListeners.size > 0) {
            synchronized(synObject) {
                listener = oneTimeListeners.removeFirst()
            }
            invoker.invoke(method, args, listener!!)
        }
        updateHasListeners()
    }

    fun hasListeners(): Boolean {
        return hasListeners
    }

    companion object {
        private val DEFAULT_INVOKER: Invoker = DefaultInvoker()
    }
}