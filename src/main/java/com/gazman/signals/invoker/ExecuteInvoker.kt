package com.gazman.signals.invoker

import java.lang.reflect.Method
import java.util.concurrent.Executor

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
class ExecuteInvoker : Invoker {
    private var executor: Executor? = null
    fun setExecutor(executor: Executor?) {
        this.executor = executor
    }

    override fun invoke(method: Method?, args: Array<Any?>?, listener: Any) {
        executor!!.execute(BaseInvoker(method, args, listener))
    }
}