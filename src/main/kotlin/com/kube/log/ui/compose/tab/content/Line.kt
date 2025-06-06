package com.kube.log.ui.compose.tab.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.ripple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kube.log.service.coloring.Rules
import com.kube.log.service.coloring.rules.ColoringQueryRule
import com.kube.log.service.coloring.rules.ColoringRule
import com.kube.log.service.search.query.Query
import com.kube.log.util.Item
import com.kube.log.util.ShowMoreAfterItem
import com.kube.log.util.ShowMoreBeforeItem
import com.kube.log.util.VirtualItem

object Code {
    val unspecified: SpanStyle = SpanStyle(Color.Unspecified)
    val green: SpanStyle = SpanStyle(Color(0xFF2E7D32))
    val yellow: SpanStyle = SpanStyle(Color(0xFFF57F17))
    val red: SpanStyle = SpanStyle(Color(0xFFB71C1C))
    val blue: SpanStyle = SpanStyle(Color(0xFF2979FF))
    val purple: SpanStyle = SpanStyle(Color(0xFF6200EA))
    val gray: SpanStyle = SpanStyle(Color(0xFF616161))
    val marked: SpanStyle = red.copy(textDecoration = TextDecoration.Underline)
}

private fun styleText(item: String, queryColoringRule: ColoringQueryRule?) = buildAnnotatedString {
    withStyle(Code.unspecified) {
        append(item)

        addStyle(Code.blue, item, Rules.HTTP_METHODS_RULE)
        addStyle(Code.red, item, Rules.ERROR_LOG_LEVEL_RULE)
        addStyle(Code.yellow, item, Rules.WARN_LOG_LEVEL_RULE)
        addStyle(Code.green, item, Rules.INFO_LOG_LEVEL_RULE)
        addStyle(Code.blue, item, Rules.EXTRACT_VALUES_FROM_FIRST_3_BRACKETS_RULE)
        addStyle(Code.purple, item, Rules.EXTRACT_VALUES_FROM_SECOND_BRACKETS_RULE)
//        addStyle(Code.gray, item, Rules.EXTRACT_VALUES_FROM_BRACKETS_RULE)
//        addStyle(Code.gray, item, Rules.EXTRACT_VALUES_RULE)
//        addStyle(Code.gray, item, Rules.IP_RULE)
//        addStyle(Code.gray, item, Rules.EMAIL_RULE)
//        addStyle(Code.gray, item, Rules.QQ_ID_RULE)
        queryColoringRule?.let { addStyle(Code.marked, item, it) }
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, rule: ColoringRule) {
    for (result in rule.findFragments(text)) {
        addStyle(style, result)
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, range: IntRange) {
    addStyle(style, range.first, range.last + 1)
}

val LINE_HEIGHT = 14.sp

@Composable
fun Line(
    item: VirtualItem<String>, query: Query?, onPrevClick: () -> Unit, onAfterClick: () -> Unit,
    modifier: Modifier = Modifier
) = when (item) {
    is Item -> ItemLine(item, query, modifier)
    is ShowMoreAfterItem -> ShowAfterLine(onAfterClick, modifier)
    is ShowMoreBeforeItem -> ShowBeforeLine(onPrevClick, modifier)
}

@Composable
fun ItemLine(
    item: Item<String>, query: Query?,
    modifier: Modifier = Modifier
) {
    val markLine = remember(item, query) {
        query?.check(item.value) ?: false
    }
    val queryColoringRule = remember(query) {
        query?.let { ColoringQueryRule(it) }
    }
    val text = remember(item, queryColoringRule) {
        styleText(item.value, queryColoringRule)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier = modifier
        .hoverable(interactionSource)
        .indication(interactionSource, ripple())

    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = LINE_HEIGHT,
        fontFamily = FontFamily.Monospace,
        modifier = clickModifier
            .let { if (markLine) it.background(MaterialTheme.colorScheme.surface) else it }
    )
    Text(
        "\n",
        lineHeight = 0.sp,
        modifier = Modifier.size(1.dp)
    )
}

@Composable
fun ShowBeforeLine(
    onPrevClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DisableSelection {
        Text(
            "Show more before...",
            style = MaterialTheme.typography.labelMedium,
            lineHeight = LINE_HEIGHT,
            textAlign = TextAlign.Center,
            modifier = modifier.clickable { onPrevClick() }
        )
    }
}

@Composable
fun ShowAfterLine(
    onAfterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DisableSelection {
        Text(
            "Show more after...",
            style = MaterialTheme.typography.labelMedium,
            lineHeight = LINE_HEIGHT,
            textAlign = TextAlign.Center,
            modifier = modifier.clickable { onAfterClick() }
        )
    }
}