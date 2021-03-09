package com.gazman.signals.context

import android.content.Context
import android.content.ContextWrapper

class AppContext : ContextWrapper(null) {

    public override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}