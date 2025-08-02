package com.kube.log.service.search

import androidx.compose.runtime.Immutable
import com.kube.log.service.search.query.Query

@Immutable
data class CompileQueryResult(
    val query: Query,
    val errors: List<String>
)