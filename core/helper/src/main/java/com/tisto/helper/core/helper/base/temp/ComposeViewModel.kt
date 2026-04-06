//package com.tisto.helper.core.helper.base
//
//import androidx.compose.foundation.ScrollState
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.grid.LazyGridState
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import com.tisto.helper.core.helper.base.BaseViewModel
//import com.tisto.helper.core.helper.base.BaseUiState
//import com.tisto.kmp.helper.utils.SnackbarType
//import com.tisto.kmp.helper.utils.UiEffect
//import com.tisto.kmp.helper.utils.UiEvent
//import com.tisto.helper.core.helper.component.showError
//import com.tisto.helper.core.helper.component.showInfo
//import com.tisto.helper.core.helper.component.showSuccess
//import com.tisto.helper.core.helper.component.showWarning
//import kotlinx.coroutines.flow.SharedFlow
//
//// ============================================
//// COMPOSE-SPECIFIC UI STATE EXTENSION
//// ============================================
//data class ComposeUiState<T>(
//    val baseState: BaseUiState<T>,
//    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
//    val formScrollState: ScrollState = ScrollState(0),
//    val listScrollState: LazyListState = LazyListState(),
//    val gridScrollState: LazyGridState = LazyGridState(),
//)
//
//// ============================================
//// EVENT COLLECTOR (Compose-specific)
//// ============================================
//suspend fun SharedFlow<UiEvent>?.collectEvent(
//    onError: (String) -> Unit = {},
//    onSuccess: (String) -> Unit = {},
//    onNavigate: (String) -> Unit = {},
//    onAction: (String) -> Unit = {},
//    onShowSnackbar: (message: String, type: SnackbarType) -> Unit = { _, _ -> },
//) {
//    this?.collect { event ->
//        when (event) {
//            is UiEvent.Error -> onError(event.message)
//            is UiEvent.Success -> {
//                onSuccess(event.message)
//                println(event.message)
//            }
//            is UiEvent.Navigate -> onNavigate(event.route)
//            is UiEvent.Action -> onAction(event.action)
//            is UiEvent.Snackbar -> onShowSnackbar(event.message, event.type)
//        }
//    }
//}
//
//// ============================================
//// COMPOSE UI EFFECT OBSERVER
//// ============================================
//@Composable
//fun <REQ> ObserveUiEffect(
//    vm: BaseViewModel<REQ>,
//    snackbarHostState: SnackbarHostState = SnackbarHostState()
//) {
//    LaunchedEffect(vm) {
//        vm.effect.collect { e ->
//            when (e) {
//                is UiEffect.Toast -> {
//                    when (e.type) {
//                        SnackbarType.SUCCESS -> snackbarHostState.showSuccess(e.message)
//                        SnackbarType.ERROR -> snackbarHostState.showError(e.message)
//                        SnackbarType.WARNING -> snackbarHostState.showWarning(e.message)
//                        SnackbarType.INFO -> snackbarHostState.showInfo(e.message)
//                    }
//                }
//            }
//        }
//    }
//}