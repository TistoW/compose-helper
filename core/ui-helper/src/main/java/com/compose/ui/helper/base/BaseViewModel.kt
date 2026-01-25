package com.compose.ui.helper.base

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.compose.data.helper.network.Resource
import com.compose.data.helper.network.State
import com.compose.ui.helper.model.FilterItem
import com.compose.ui.helper.ui.component.showError
import com.compose.ui.helper.ui.component.showInfo
import com.compose.ui.helper.ui.component.showSuccess
import com.compose.ui.helper.ui.component.showWarning
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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


data class BaseUiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val isLoadingProcess: Boolean = false,
    val isRefreshing: Boolean = false,
    val screen: String = ScreenTypes.list,
    val snackbarHost: SnackbarHostState = SnackbarHostState(),

    val search: String? = null,
    val filters: List<FilterItem> = emptyList(),
    val successMessage: String? = null,
    val backAction: String? = null,
    val formScrollState: ScrollState = ScrollState(0),
    val listScrollState: LazyListState = LazyListState(),

    // Pagination
    val page: Int = 1,
    val perPage: Int = 20,
    val totalPage: Int = 1,
    val loadingSize: Int = 0, // yg sedang di load saat ini
    val loadedCount: Int = 0, // yg total semua sudah di load
    val totalSize: Int = 0,
    val hasMore: Boolean = false,
) {
    val isSearching: Boolean
        get() = !search.isNullOrEmpty()

    val isEmpty: Boolean
        get() = loadingSize == 0 && loadedCount == 0


}

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

    fun toastWaring(message: String) {
        sendToast(message, SnackbarType.WARNING)
    }

    fun updateUiState(
        reducer: BaseUiState<STATE>.() -> BaseUiState<STATE>
    ) {
        _uiState.update { it.reducer() }
    }

    protected fun setState(
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

    fun navigate(screen: String) {
        _uiState.update { it.copy(screen = screen) }
    }

    protected fun <T> Flow<Resource<T>>.collectResource(
        isLoading: Boolean = true,
        toastError: Boolean = true,
        onError: (String) -> Unit = {},
        onComplete: () -> Unit = {},
        onSuccessAllRes: (Resource<T>) -> Unit = {},
        onSuccess: (T) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                this@collectResource
                    .catch { e -> onError(e.message ?: "Unknown Error") }
                    .collect { res ->
                        when (res.state) {
                            State.LOADING -> if (isLoading) setLoading(true)
                            State.SUCCESS -> {
                                setLoading(false)
                                setLoadingProcess(false)
                                onSuccessAllRes(res)
                                res.body?.let { onSuccess(it) } ?: onError("Data is null")
                            }

                            State.ERROR -> {
                                setLoading(false)
                                setLoadingProcess(false)
                                val message = res.message
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
            } finally {
                onComplete()
            }
        }
    }

    fun <T> onLoaded(page: Int, currentList: List<T>, res: Resource<List<T>>) {
        val items = res.body ?: listOf()
        updateUiState {
            copy(
                page = page,
                hasMore = items.size >= perPage,
                totalPage = res.lastPage,
                totalSize = res.total,
                loadingSize = items.size,
                loadedCount = currentList.size + items.size
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

abstract class StatefulViewModel<STATE : BaseState<R, I, STATE>, R, I> : BaseViewModel<STATE>() {

    fun getItems() = uiState.value.data?.items ?: emptyList()
    fun getRequest() = uiState.value.data?.request
    fun getItem() = uiState.value.data?.item

    fun updateItems(update: List<I>.() -> List<I>) {
        setState { copies(items = items.update()) }
    }

    fun updateItem(update: I.() -> I?) {
        setState { copies(item = item?.update()) }
    }

    fun updateRequest(update: R.() -> R) {
        setState { copies(request = request?.update()) }
    }

    fun <T> onLoaded(page: Int, res: Resource<List<T>>) {
        val items = res.body ?: listOf()
        updateUiState {
            copy(
                page = page,
                hasMore = items.size >= perPage,
                totalPage = res.lastPage,
                totalSize = res.total,
                loadingSize = items.size,
                loadedCount = getItems().size + items.size
            )
        }
    }
}

@Composable
fun <REQ> ObserveUiEffect(
    vm: BaseViewModel<REQ>,
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
