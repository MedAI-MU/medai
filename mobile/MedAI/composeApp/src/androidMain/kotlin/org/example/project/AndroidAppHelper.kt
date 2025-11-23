package org.example.project

import android.content.Context
import java.lang.ref.WeakReference

object AndroidAppHelper {
    private var contextRef: WeakReference<Context>? = null

    fun init(context: Context) {
        contextRef = WeakReference(context)
    }

    fun getContext(): Context {
        return contextRef?.get() ?: throw IllegalStateException("Context not initialized! Call AndroidAppHelper.init(context) in your MainActivity.")
    }
}
