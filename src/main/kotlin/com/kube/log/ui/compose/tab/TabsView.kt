package com.kube.log.ui.compose.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kube.log.model.PodInfo
import com.kube.log.ui.compose.tab.content.TabContent

@Composable
fun TabsView(
    logTabsState: LogTabsState,
    onOpenPod: (PodInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SecondaryScrollableTabRow(
            selectedTabIndex = logTabsState.selection,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            logTabsState.tabs.forEachIndexed { index, logTab ->
                CustomTab(
                    podInfoState = logTab.podInfoState,
                    selected = logTabsState.selection == index,
                    onClick = { logTabsState.selectTab(index) },
                    onClose = { logTabsState.close(logTab) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        logTabsState.active?.let {
            TabContent(it, onOpenPod)
        }
    }
}