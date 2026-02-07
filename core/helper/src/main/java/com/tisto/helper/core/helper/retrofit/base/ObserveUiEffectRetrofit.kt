package com.tisto.helper.core.helper.retrofit.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tisto.helper.core.helper.base.SnackbarType
import com.tisto.helper.core.helper.base.UiEffect
import com.tisto.helper.core.helper.component.showError
import com.tisto.helper.core.helper.component.showInfo
import com.tisto.helper.core.helper.component.showSuccess
import com.tisto.helper.core.helper.component.showWarning

@Composable
fun <REQ> ObserveUiEffectRetrofit(
    vm: BaseRetrofitViewModel<REQ>,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.effect.collect { e ->
            when (e) {
                is UiEffect.Toast -> {
                    when (e.type) {
                        SnackbarType.SUCCESS -> uiState.snackbarHost.showSuccess(e.message)
                        SnackbarType.ERROR -> uiState.snackbarHost.showError(e.message)
                        SnackbarType.WARNING -> uiState.snackbarHost.showWarning(e.message)
                        SnackbarType.INFO -> uiState.snackbarHost.showInfo(e.message)
                    }
                }
            }
        }
    }
}
