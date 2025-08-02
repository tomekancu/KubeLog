package com.kube.log.util

import androidx.compose.runtime.Immutable

@Immutable
sealed class LoadableResult<out T> {
    object Loading : LoadableResult<Nothing>()
    data class Value<out T>(val value: T) : LoadableResult<T>()
    data class Error(val error: Throwable) : LoadableResult<Nothing>()
}