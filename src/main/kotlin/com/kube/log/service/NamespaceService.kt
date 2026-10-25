package com.kube.log.service

import com.kube.log.util.LoggerUtils.logger
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object NamespaceService {
    private val log = logger()

    suspend fun readAllNamespaceSuspending(): List<String> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            log.info("Start read all namespaces")
            val p = ProcessBuilder(Kubectl.path, "get", "namespaces",
                "-o=jsonpath={range .items[*].metadata.name}{@}{\"\\n\"}{end}")
                .start()
            continuation.invokeOnCancellation {
                p.destroy()
            }

            val output = p.inputStream.bufferedReader().use { it.readText() }
            val errorOutput = p.errorStream.bufferedReader().use { it.readText() }

            val exitValue = p.waitFor()
            log.info("Stop read all namespaces $exitValue")
            if (exitValue == 0 && output.isNotEmpty()) {
                continuation.resume(output.lines().filter { it.isNotEmpty() })
            } else {
                continuation.resumeWithException(IllegalStateException("Wrong output\n$output$errorOutput\nexit code: $exitValue"))
            }
        }
    }

    suspend fun readCurrentNamespaceSuspending(): String = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            log.info("Start read current namespace")
            val p = ProcessBuilder(Kubectl.path, "config", "view", "--minify",
                "-o=jsonpath={range .contexts[*].context.namespace}{@}{\"\\n\"}{end}")
                .start()
            continuation.invokeOnCancellation {
                p.destroy()
            }

            val output = p.inputStream.bufferedReader().use { it.readText().trim() }
            val errorOutput = p.errorStream.bufferedReader().use { it.readText() }

            val exitValue = p.waitFor()
            log.info("Stop read current namespace $exitValue")
            if (exitValue == 0) {
                continuation.resume(output.ifEmpty { "default" })
            } else {
                continuation.resumeWithException(IllegalStateException("Wrong output\n$output$errorOutput\nexit code: $exitValue"))
            }
        }
    }
}

