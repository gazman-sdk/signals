package com.gazman.signals.invoker

import java.lang.reflect.Method

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
open class DefaultInvoker : Invoker {
    private var baseInvoker = BaseInvoker()

    override fun invoke(method: Method?, args: Array<Any?>?, listener: Any) {
        baseInvoker.method = method
        baseInvoker.args = args
        baseInvoker.listener = listener
        baseInvoker.run()
    }
}