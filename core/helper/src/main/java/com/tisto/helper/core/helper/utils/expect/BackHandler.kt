package com.tisto.helper.core.helper.utils.expect

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun CustomBackHandler( // biar sama jika nanti move ke KMP
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    BackHandler(enabled, onBack)
}
