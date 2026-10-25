package com.kube.log.service

import java.io.File

object Kubectl {
    val path: String = listOf("/opt/homebrew/bin/kubectl", "/usr/local/bin/kubectl", "/usr/bin/kubectl")
        .firstOrNull { File(it).canExecute() } ?: "kubectl"
}
