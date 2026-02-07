package com.tisto.helper.core.helper.utils.expect

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.DisposableHandle

@Composable
fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    BackHandler(enabled, onBack)
}
