package com.tisto.helper.core.helper.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tisto.helper.core.helper.source.network.Resource
import com.tisto.helper.core.helper.utils.ext.def
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface BaseState<R, I, SELF : BaseState<R, I, SELF>> {
    val items: List<I>
    val request: R?
    val item: I?

    fun copies(
        items: List<I> = this.items,
        request: R? = this.request,
        item: I? = this.item
    ): SELF
}

sealed interface UiEvent {
    data class Error(val message: String) : UiEvent
    data class Success(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data class Action(val action: String) : UiEvent
    data class Snackbar(val message: String, val type: SnackbarType = SnackbarType.SUCCESS) :
        UiEvent
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
    onAction: (String) -> Unit = {},
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit = { _, _ -> },
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

            is UiEvent.Snackbar -> {
                onShowSnackbar(event.message, event.type)
            }
        }
    }
}

abstract class BaseViewModel<STATE> : ViewModel() {

    protected abstract fun initialState(): STATE

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    protected val _uiState = MutableStateFlow(BaseUiState(data = initialState()))
    val uiState: StateFlow<BaseUiState<STATE>> = _uiState.asStateFlow()

    private val _effect = Channel<UiEffect>(Channel.BUFFERED)
    val effect: Flow<UiEffect> = _effect.receiveAsFlow()

    protected fun sendToast(message: String, type: SnackbarType = SnackbarType.INFO) {
        viewModelScope.launch { _effect.send(UiEffect.Toast(message, type)) }
    }

    protected fun emitError(message: String) {
        viewModelScope.launch {
            _event.emit(UiEvent.Error(message))
        }
    }

    protected fun emitSuccess(message: String) {
        viewModelScope.launch {
            _event.emit(UiEvent.Success(message))
        }
    }

    protected fun emitAction(message: String) {
        viewModelScope.launch {
            _event.emit(UiEvent.Action(message))
        }
    }

    protected fun emitSnackBar(message: String, type: SnackbarType) {
        viewModelScope.launch {
            _event.emit(UiEvent.Snackbar(message, type))
        }
    }

    fun toastSuccess(message: String) {
        sendToast(message, SnackbarType.SUCCESS)
    }

    fun toastError(message: String) {
        sendToast(message, SnackbarType.ERROR)
    }

    fun toastInfo(message: String) {
        sendToast(message, SnackbarType.INFO)
    }

    fun toastWarning(message: String) {
        sendToast(message, SnackbarType.WARNING)
    }

    fun updateUiState(
        reducer: BaseUiState<STATE>.() -> BaseUiState<STATE>
    ) {
        _uiState.update { it.reducer() }
    }

    fun updateState(
        reducer: STATE.() -> STATE
    ) {
        _uiState.update {
            it.copy(data = it.data?.reducer())
        }
    }

    protected fun setLoadingProcess(value: Boolean) {
        updateUiState { copy(isLoadingProcess = value) }
    }

    protected fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    protected fun setRefreshing(value: Boolean) {
        _uiState.update { it.copy(isRefreshing = value) }
    }

    fun navigate(screen: String) {
        _uiState.update { it.copy(screen = screen) }
    }

    protected fun <R> Flow<Resource<R>>.collectResource(
        isLoading: Boolean = true,
        toastError: Boolean = true,
        onError: (String) -> Unit = {},
        onSuccess: (R) -> Unit = {},
        onSuccessAllRes: (Resource<R>) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            if (isLoading) setLoading(true)
                        }

                        is Resource.Success -> {
                            setLoading(false)
                            setLoadingProcess(false)
                            onSuccess(result.data)
                            onSuccessAllRes(result)
                            updateUiState {
                                copy(
                                    totalPage = result.lastPage.def(1),
                                    totalSize = result.total.def(0)
                                )
                            }
                        }

                        is Resource.Error -> {
                            setLoading(false)
                            setLoadingProcess(false)
                            val message = result.message ?: "Unknown error"
                            if (toastError) toastError(message)
                            onError(message)
                        }
                    }
                }
            } catch (e: Exception) {
                setLoading(false)
                setLoadingProcess(false)
                val message = e.message ?: "Unknown error"
                if (toastError) toastError(message)
                onError(e.message ?: "Exception error")
            }

        }
    }

    fun <T> onLoaded(page: Int, currentItems: List<T>, items: List<T>) {
        updateUiState {
            copy(
                hasMore = items.size >= pageLimit,
                isRefreshing = false,
                page = page,
                loadingSize = items.size,
                loadedCount = currentItems.size + items.size
            )
        }
    }

}

class StateHandler<STATE, R, I>(
    private val getState: () -> STATE?,
    private val setState: (STATE.() -> STATE) -> Unit
) where STATE : BaseState<R, I, STATE> {

    fun getItems(): List<I> = getState()?.items ?: emptyList()

    fun getRequest(): R? = getState()?.request

    fun getItem(): I? = getState()?.item

    fun updateItems(update: List<I>.() -> List<I>) {
        setState { copies(items = items.update()) }
    }

    fun updateItem(update: I.() -> I?) {
        setState { copies(item = item?.update()) }
    }

    fun updateRequest(update: R.() -> R) {
        setState { copies(request = request?.update()) }
    }

}