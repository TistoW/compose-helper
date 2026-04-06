package com.tisto.helper.core.helper.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter


@Composable
fun ImageVector.toPainter(): Painter {
    return rememberVectorPainter(image = this)
}