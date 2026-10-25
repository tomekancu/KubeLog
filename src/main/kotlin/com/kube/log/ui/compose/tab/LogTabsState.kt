package com.kube.log.ui.compose.tab

import androidx.compose.runtime.*

@Stable
class LogTabsState {
    var selection by mutableStateOf(0)
        private set
    var tabs = mutableStateListOf<LogTab>()
        private set

    val openAppsFlow = snapshotFlow { tabs.map { it.podInfo.calculatedAppName }.toSet() }

    val active: LogTab?
        get() = selection.let { tabs.getOrNull(it) }

    fun selectTab(index: Int) {
        selection = index
    }

    fun open(tab: LogTab) {
        tab.init()
        tabs.add(tab)
        selection = tabs.lastIndex
    }

    fun close(logTab: LogTab) {
        logTab.destroy()
        val index = tabs.indexOf(logTab)
        tabs.remove(logTab)
        if (index in 0 until selection) {
            selection--
        }
        selection = selection.coerceAtMost(tabs.lastIndex)
    }

    fun closeAll() {
        tabs.forEach { it.destroy() }
        tabs.clear()
        selection = 0
    }
}