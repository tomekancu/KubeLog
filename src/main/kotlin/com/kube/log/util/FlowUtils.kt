package com.kube.log.util

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

object FlowUtils {
    fun <T, R> Flow<T>.zipWithNext(transform: (T?, T) -> R): Flow<R> = flow {
        var prev: T? = null
        collect { value ->
            emit(transform(prev, value))
            prev = value
        }
    }

    fun <T> Flow<T>.batched(window: Duration): Flow<List<T>> = channelFlow {
        val buffer = mutableListOf<T>()
        val mutex = Mutex()

        suspend fun flush() = mutex.withLock {
            if (buffer.isNotEmpty()) {
                send(buffer.toList())
                buffer.clear()
            }
        }

        val ticker = launch {
            while (true) {
                delay(window)
                flush()
            }
        }
        collect { mutex.withLock { buffer += it } }
        ticker.cancelAndJoin()
        flush()
    }
}
