package com.gazman.signals.invoker

import java.lang.reflect.Method

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
interface Invoker {
    operator fun invoke(method: Method?, args: Array<Any?>?, listener: Any)
}