package com.tisto.helper.core.helper.base

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tisto.helper.core.helper.retrofit.network.ResourceRetrofit
import com.tisto.helper.core.helper.retrofit.model.FilterItem
import com.tisto.helper.core.helper.component.showError
import com.tisto.helper.core.helper.component.showInfo
import com.tisto.helper.core.helper.component.showSuccess
import com.tisto.helper.core.helper.component.showWarning
import com.tisto.helper.core.helper.retrofit.base.BaseRetrofitViewModel
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


data class BaseUiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val isLoadingProcess: Boolean = false,
    val isRefreshing: Boolean = false,
    val screen: String = ScreenTypes.list,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),

    val search: String? = null,
    val filters: List<FilterItem> = emptyList(),
    val successMessage: String? = null,
    val backAction: String? = null,
    val formScrollState: ScrollState = ScrollState(0),
    val listScrollState: LazyListState = LazyListState(),

    // Pagination
    val page: Int = 1,
    val pageLimit: Int = 10,
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

abstract class StatefulViewModel<STATE : BaseState<R, I, STATE>, R, I> :
    BaseRetrofitViewModel<STATE>() {

    fun getItems() = uiState.value.data?.items ?: emptyList()
    fun getRequest() = uiState.value.data?.request
    fun getItem() = uiState.value.data?.item

    fun updateItems(update: List<I>.() -> List<I>) {
        updateState { copies(items = items.update()) }
    }

    fun updateItem(update: I.() -> I?) {
        updateState { copies(item = item?.update()) }
    }

    fun updateRequest(update: R.() -> R) {
        updateState { copies(request = request?.update()) }
    }

    fun <T> onLoaded(page: Int, res: ResourceRetrofit<List<T>>) {
        val items = res.body ?: listOf()
        updateUiState {
            copy(
                page = page,
                hasMore = items.size >= pageLimit,
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
                        SnackbarType.SUCCESS -> uiState.snackbarHostState.showSuccess(e.message)
                        SnackbarType.ERROR -> uiState.snackbarHostState.showError(e.message)
                        SnackbarType.WARNING -> uiState.snackbarHostState.showWarning(e.message)
                        SnackbarType.INFO -> uiState.snackbarHostState.showInfo(e.message)
                    }
                }
            }
        }
    }
}
