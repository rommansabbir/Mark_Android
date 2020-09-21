package com.rommansabbir.androidtraining

import kotlinx.coroutines.launch

class MainVM : BaseViewModel() {
    internal fun checkEmail(email: String, onSuccess: () -> Unit) {
        ioScope.launch {
            isLoading.postValue(true)
            executeCoroutine(
                {
                    when (email.isNotEmpty()) {
                        true -> {
                            isLoading.postValue(false)
                            mainScope.launch {
                                onSuccess.invoke()
                            }
                        }
                        else -> {
                            throw Exception("Already exist")
                        }
                    }
                },
                {
                    isLoading.postValue(false)
                    hasError.postValue(it)
                }
            )
        }
    }

    internal fun checkPassword(password: String, onSuccess: () -> Unit) {
        ioScope.launch {
            isLoading.postValue(true)
            executeCoroutine(
                {
                    when (password.length > 8) {
                        true -> {
                            isLoading.postValue(false)
                            mainScope.launch {
                                onSuccess.invoke()
                            }
                        }
                        else -> {
                            throw Exception("Password not strong enough")
                        }
                    }
                },
                {
                    isLoading.postValue(false)
                    hasError.postValue(it)
                }
            )
        }
    }
}

suspend fun executeCoroutine(success: suspend () -> Unit, error: suspend (Exception) -> Unit) {
    try {
        success.invoke()
    } catch (e: Exception) {
        error.invoke(e)
    }
}