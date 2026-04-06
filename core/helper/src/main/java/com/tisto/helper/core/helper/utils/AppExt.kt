package com.tisto.helper.core.helper.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.tisto.helper.core.helper.source.request.SearchRequest
import com.tisto.kmp.helper.utils.ext.logs
import com.tisto.kmp.helper.android.utils.ext.toJson
import com.tisto.kmp.helper.utils.ext.translateJson
import java.util.HashMap

fun <T> List<T>.insertAt(value: T, index: Int = 1): List<T> {
    return when {
        index <= 0 -> listOf(value) + this
        index >= size -> this + value
        else -> take(index) + value + drop(index)
    }
}

@Composable
fun ImageVector.toPainter(): Painter {
    return rememberVectorPainter(image = this)
}