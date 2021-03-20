package com.gazman.signals

import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Ilya Gazman on 2/24/2015.
 */
class SignalsHelper {
    private val removables = ArrayList<Runnable>()
    private var registerCallback: Runnable? = null
    private var isRegistered = false

    fun setRegisterCallback(registerCallback: Runnable?) {
        this.registerCallback = registerCallback
    }

    /**
     * A helper method to manage the registration processes
     * It simply make sure to not call the registerCallback more than once per register
     *
     * @return if the registration made
     */
    fun register(): Boolean {
        if (isRegistered) {
            return false
        }
        isRegistered = true
        registerCallback!!.run()
        return true
    }

    /**
     * Change the state to unregister and call to removeAll(), it does not removes the registerCallback
     * to removeIt call setRegisterCallback(null), how ever most of the time it's not really necessary...
     */
    fun unregister() {
        isRegistered = false
    }

    /**
     * Will call to signal.addListener(listener)
     */
    fun <T : Any> addListener(signal: Signal<T>, listener: T) {
        signal.addListener(listener)
        removables.add { signal.removeListener(listener) }
    }

    fun <T : Any> addListener(type: KClass<T>, listener: T) {
        addListener(type.java, listener)
    }

    /**
     * Will call to Signals.inject(signal).addListener(listener)
     */
    fun <T> addListener(type: Class<T>, listener: T) {
        val signal = Signals.signal(type)
        signal.addListener(listener)
        removables.add { signal.removeListener(listener) }
    }

    /**
     * Will call to Signals.signal(signal).addListenerOnce(listener)
     */
    fun <T : Any> addListenerOnce(type: KClass<T>, listener: T) {
        addListenerOnce(Signals.signal(type), listener)
    }

    /**
     * Will call to [signal.addListenerOnce(listener)][Signal.addListenerOnce]
     */
    fun <T> addListenerOnce(signal: Signal<T>, listener: T) {
        signal.addListenerOnce(listener)
        removables.add { signal.removeListener(listener) }
    }

    fun <T : Any> removeListener(type: KClass<T>, listener: T) {
        removeListener(type.java, listener)
    }

    /**
     * Will call to Signals.signal(signal).removeListener(listener)
     */
    fun <T> removeListener(type: Class<T>, listener: T) {
        val signal = Signals.signal(type)
        signal.removeListener(listener)
    }

    /**
     * Will call to signal.removeListener(listener)
     */
    fun <T> removeListener(signal: Signal<T>, listener: T) {
        signal.removeListener(listener)
    }

    /**
     * Will remove all signals that been added using this SignalsHelper.
     */
    fun removeAll() {
        for (removable in removables) {
            removable.run()
        }
        removables.clear()
    }

    /**
     * Are there any listeners registered with this helper
     */
    fun hasListeners(): Boolean {
        return removables.isNotEmpty()
    }
}