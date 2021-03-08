package com.gazman.signals.invoker

import java.lang.reflect.Method
import java.util.concurrent.Executor

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
class ExecuteInvoker(private val executor: Executor) : Invoker {


    override fun invoke(method: Method?, args: Array<Any?>?, listener: Any) {
        executor.execute(BaseInvoker(method, args, listener))
    }
}