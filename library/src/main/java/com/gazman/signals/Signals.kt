package com.gazman.signals

import android.util.Log
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Ilya Gazman on 2/24/2015.
 */
object Signals {
    private val map = HashMap<Class<*>, Signal<*>>()

    fun <T : Any> signal(type: KClass<T>): Signal<T> {
        return signal(type.java)
    }

    /**
     * Will get you a signal from the given interface type, there will be only one instance
     * of it in the process
     *
     * @param type the signal type
     * @return Signal from given type
     */
    @JvmStatic
    fun <T> signal(type: Class<T>): Signal<T> {
        @Suppress("UNCHECKED_CAST")
        return map.getOrPut(type, { localSignal(type) }) as Signal<T>
    }

    fun <T : Any> localSignal(type: KClass<T>): Signal<T> {
        return localSignal(type.java)
    }

    /**
     * Create new signal from given type
     *
     * @param type the interface type
     * @return Signal from given type
     */
    @JvmStatic
    fun <T> localSignal(type: Class<T>): Signal<T> {
        return Signal(type)
    }

    fun <T : Any> log(tClass: KClass<T>, tag: String?): T {
        return log(tClass.java, tag)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @JvmStatic
    fun <T> log(tClass: Class<T>, tag: String?): T {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(tClass.classLoader, arrayOf<Class<*>>(tClass)) { _, method: Method, args: Array<Any>? ->
            val stringBuilder = StringBuilder()
            if (args != null) {
                for (arg in args) {
                    stringBuilder.append(arg).append(" ")
                }
            }
            Log.d(tag, method.name + " " + stringBuilder.toString())
            val returnType = method.returnType
            if (returnType.isPrimitive) {
                if (returnType.isAssignableFrom(Boolean::class.javaPrimitiveType)) {
                    return@newProxyInstance false
                }
                if (returnType.isAssignableFrom(Int::class.javaPrimitiveType)) {
                    return@newProxyInstance 0
                }
                if (returnType.isAssignableFrom(Float::class.javaPrimitiveType)) {
                    return@newProxyInstance 0
                }
                if (returnType.isAssignableFrom(Long::class.javaPrimitiveType)) {
                    return@newProxyInstance 0
                }
                if (returnType.isAssignableFrom(Byte::class.javaPrimitiveType)) {
                    return@newProxyInstance 0
                }
                if (returnType.isAssignableFrom(Double::class.javaPrimitiveType)) {
                    return@newProxyInstance 0
                }
                if (returnType.isAssignableFrom(Short::class.javaPrimitiveType)) {
                    return@newProxyInstance 0
                }
            }
            null
        } as T
    }
}