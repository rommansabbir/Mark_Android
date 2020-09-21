package com.rommansabbir.androidtraining

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : AppViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var hasError: MutableLiveData<Exception> = MutableLiveData()

    private val MAIN_JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.MAIN_JOB_KEY"
    private val IO_JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.IO_JOB_KEY"
    private val DEFAULT_JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.DEFAULT_JOB_KEY"

    val mainScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(MAIN_JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                MAIN_JOB_KEY,
                CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            )
        }

    val ioScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(IO_JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                IO_JOB_KEY,
                CloseableCoroutineScope(SupervisorJob() + Dispatchers.IO)
            )
        }

    val defaultScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(DEFAULT_JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                DEFAULT_JOB_KEY,
                CloseableCoroutineScope(SupervisorJob() + Dispatchers.Default)
            )
        }

    internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context

        override fun close() {
            coroutineContext.cancel()
        }
    }
}

