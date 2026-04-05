//package com.tisto.helper.core.helper.base
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.tisto.helper.core.helper.source.network.Resource
//import com.tisto.helper.core.helper.retrofit.network.ResourceRetrofit
//import com.tisto.helper.core.helper.utils.ext.def
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//// ============================================
//// NETWORK-SPECIFIC VIEWMODEL
//// ============================================
//abstract class BaseRetrofitViewModel<STATE> : BaseViewModel<STATE>()
//
//abstract class StatefulViewModel<STATE : BaseState<R, I, STATE>, R, I> :
//    BaseRetrofitViewModel<STATE>() {
//
//    fun getItems() = uiState.value.data?.items ?: emptyList()
//    fun getRequest() = uiState.value.data?.request
//    fun getItem() = uiState.value.data?.item
//
//    fun updateItems(update: List<I>.() -> List<I>) {
//        updateState { copies(items = items.update()) }
//    }
//
//    fun updateItem(update: I.() -> I?) {
//        updateState { copies(item = item?.update()) }
//    }
//
//    fun updateRequest(update: R.() -> R) {
//        updateState { copies(request = request?.update()) }
//    }
//
//    fun <T> onLoaded(page: Int, res: ResourceRetrofit<List<T>>) {
//        val items = res.body ?: listOf()
//        updateUiState {
//            copy(
//                page = page,
//                hasMore = items.size >= pageLimit,
//                totalPage = res.lastPage,
//                totalSize = res.total,
//                loadingSize = items.size,
//                loadedCount = getItems().size + items.size
//            )
//        }
//    }
//}
//
//abstract class BaseViewModel<STATE> : ViewModel() {
//
//    protected abstract fun initialState(): STATE
//
//    private val _event = MutableSharedFlow<UiEvent>()
//    val event = _event.asSharedFlow()
//
//    protected val _uiState = MutableStateFlow(
//        BaseUiState(
//            data = initialState()
//        )
//    )
//    val uiState: StateFlow<BaseUiState<STATE>> = _uiState.asStateFlow()
//
//    private val _effect = Channel<UiEffect>(Channel.BUFFERED)
//    val effect: Flow<UiEffect> = _effect.receiveAsFlow()
//
//    protected fun sendToast(message: String, type: SnackbarType = SnackbarType.INFO) {
//        viewModelScope.launch { _effect.send(UiEffect.Toast(message, type)) }
//    }
//
//    protected fun emitError(message: String) {
//        viewModelScope.launch { _event.emit(UiEvent.Error(message)) }
//    }
//
//    protected fun emitSuccess(message: String) {
//        viewModelScope.launch { _event.emit(UiEvent.Success(message)) }
//    }
//
//    protected fun emitAction(message: String) {
//        viewModelScope.launch { _event.emit(UiEvent.Action(message)) }
//    }
//
//    protected fun emitSnackBar(message: String, type: SnackbarType) {
//        viewModelScope.launch { _event.emit(UiEvent.Snackbar(message, type)) }
//    }
//
//    fun toastSuccess(message: String) = sendToast(message, SnackbarType.SUCCESS)
//    fun toastError(message: String) = sendToast(message, SnackbarType.ERROR)
//    fun toastInfo(message: String) = sendToast(message, SnackbarType.INFO)
//    fun toastWarning(message: String) = sendToast(message, SnackbarType.WARNING)
//
//    fun updateUiState(reducer: BaseUiState<STATE>.() -> BaseUiState<STATE>) {
//        _uiState.update { it.reducer() }
//    }
//
//    fun updateState(reducer: STATE.() -> STATE) {
//        _uiState.update { it.copy(data = it.data?.reducer()) }
//    }
//
//    fun setLoadingProcess(value: Boolean) {
//        updateUiState {
//            BaseUiState(
//                isLoadingProcess = value
//            )
//        }
//    }
//
//    fun setLoading(value: Boolean) {
//        _uiState.update { it.copy(isLoading = value) }
//    }
//
//    protected fun setRefreshing(value: Boolean) {
//        _uiState.update { it.copy(isRefreshing = value) }
//    }
//
//    fun navigate(screen: String) {
//        _uiState.update { it.copy(screen = screen) }
//    }
//
//    fun <T> onLoaded(page: Int, currentItems: List<T>, items: List<T>) {
//        updateUiState {
//            BaseUiState(
//                hasMore = items.size >= pageLimit,
//                isRefreshing = false,
//                page = page,
//                loadingSize = items.size,
//                loadedCount = currentItems.size + items.size
//            )
//        }
//    }
//
//    fun <R> Flow<Resource<R>>.collectResource(
//        isLoading: Boolean = true,
//        toastError: Boolean = true,
//        onError: (String) -> Unit = {},
//        onSuccess: (R) -> Unit = {},
//        onSuccessAllRes: (Resource<R>) -> Unit = {},
//    ) {
//        viewModelScope.launch {
//            try {
//                collect { result ->
//                    when (result) {
//                        is Resource.Loading -> {
//                            if (isLoading) setLoading(true)
//                        }
//
//                        is Resource.Success -> {
//                            setLoading(false)
//                            setLoadingProcess(false)
//                            onSuccess(result.data)
//                            onSuccessAllRes(result)
//                            updateUiState {
//                                copy(
//                                    totalPage = result.lastPage.def(1),
//                                    totalSize = result.total.def(0)
//                                )
//                            }
//                        }
//
//                        is Resource.Error -> {
//                            setLoading(false)
//                            setLoadingProcess(false)
//                            val message = result.message ?: "Unknown error"
//                            if (toastError) toastError(message)
//                            onError(message)
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                setLoading(false)
//                setLoadingProcess(false)
//                val message = e.message ?: "Unknown error"
//                if (toastError) toastError(message)
//                onError(e.message ?: "Exception error")
//            }
//
//        }
//    }
//}
//// ============================================
//// RESOURCE COLLECTOR EXTENSION
//// ============================================
