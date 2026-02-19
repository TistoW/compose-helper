package com.tisto.helper.core.helper.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun LoadingDialog(show: Boolean) {
    if (show) {
        OverlayBox(
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }
}