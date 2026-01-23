package com.compose.data.helper.base

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BaseUiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val screen: String = ScreenTypes.list,
    val snackbarHost: SnackbarHostState = SnackbarHostState()
)

object ScreenTypes {
    const val list = "list"
    const val form = "create"
}

object SuccessTypes {
    const val create = "create"
    const val update = "update"
    const val delete = "delete"
}

sealed interface UiEvent {
    data class Error(val message: String) : UiEvent
    data class Success(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data class Action(val action: String) : UiEvent
}

enum class SnackbarType { SUCCESS, ERROR, WARNING, INFO }
sealed interface UiEffect {
    data class Toast(
        val message: String,
        val type: SnackbarType = SnackbarType.INFO // default value
    ) : UiEffect
}


suspend fun SharedFlow<UiEvent>?.collectEvent(
    onError: (String) -> Unit = {},
    onSuccess: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onAction: (String) -> Unit = {}
) {
    this?.collect { event ->
        when (event) {
            is UiEvent.Error -> {
                onError(event.message)
            }

            is UiEvent.Success -> {
                onSuccess(event.message)
                println(event.message)
            }

            is UiEvent.Navigate -> {
                onNavigate(event.route)
            }

            is UiEvent.Action -> {
                onAction(event.action)
            }
        }
    }
}

abstract class BaseViewModel<STATE> : ViewModel() {

    protected abstract fun initialState(): STATE

    protected val _uiState = MutableStateFlow(
        BaseUiState(data = initialState())
    )

    val uiState: StateFlow<BaseUiState<STATE>> =
        _uiState.asStateFlow()

    protected fun setState(
        reducer: STATE.() -> STATE
    ) {
        _uiState.update {
            it.copy(data = it.data?.reducer())
        }
    }

    protected fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    fun navigate(screen: String) {
        _uiState.update { it.copy(screen = screen) }
    }
}

