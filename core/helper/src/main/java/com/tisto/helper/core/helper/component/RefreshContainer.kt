package com.tisto.helper.core.helper.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RefreshContainer(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    contentAlignment: Alignment = Alignment.TopCenter,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
//    if (isMobile()) {
//        PullToRefreshBox(
//            isRefreshing = isRefreshing,
//            onRefresh = onRefresh,
//            contentAlignment = contentAlignment,
//            modifier = modifier.fillMaxSize()
//        ) {
//            content()
//        }
//    } else {
//        Box(
//            modifier.fillMaxSize(),
//            contentAlignment = contentAlignment
//        ) {
//            content()
//        }
//    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        contentAlignment = contentAlignment,
        modifier = modifier.fillMaxSize()
    ) {
        content()
    }
}
