//package com.tisto.helper.core.helper.base
//
//import androidx.compose.foundation.ScrollState
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.grid.LazyGridState
//import androidx.compose.material3.SnackbarHostState
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.tisto.kmp.helper.utils.model.FilterItem
//
//// ============================================
//// BASE INTERFACES & STATES
//// ============================================
//interface BaseState<R, I, SELF : BaseState<R, I, SELF>> {
//    val items: List<I>
//    val request: R?
//    val item: I?
//
//    fun copies(
//        items: List<I> = this.items,
//        request: R? = this.request,
//        item: I? = this.item
//    ): SELF
//}
//
//data class BaseUiState<T>(
//    val data: T? = null,
//    val isLoading: Boolean = false,
//    val isLoadingProcess: Boolean = false,
//    val isRefreshing: Boolean = false,
//    val screen: String = ScreenTypes.list,
//    val search: String? = null,
//    val successMessage: String? = null,
//    val backAction: String? = null,
//
//    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
//    val filters: List<FilterItem> = emptyList(),
//
//    val formScrollState: ScrollState = ScrollState(0),
//    val listScrollState: LazyListState = LazyListState(),
//    val gridScrollState: LazyGridState = LazyGridState(),
//    // Pagination
//    val page: Int = 1,
//    val pageLimit: Int = 10,
//    val totalPage: Int = 1,
//    val loadingSize: Int = 0,
//    val loadedCount: Int = 0,
//    val totalSize: Int = 0,
//    val hasMore: Boolean = false,
//) {
//    val isSearching: Boolean
//        get() = !search.isNullOrEmpty()
//    val isEmpty: Boolean
//        get() = loadingSize == 0 && loadedCount == 0
//}
//
//// ============================================
//// CONSTANTS
//// ============================================
//object ScreenTypes {
//    const val list = "list"
//    const val form = "create"
//}
//
//object SuccessTypes {
//    const val create = "create"
//    const val update = "update"
//    const val delete = "delete"
//}
//
//// ============================================
//// EVENTS & EFFECTS
//// ============================================
//sealed interface UiEvent {
//    data class Error(val message: String) : UiEvent
//    data class Success(val message: String) : UiEvent
//    data class Navigate(val route: String) : UiEvent
//    data class Action(val action: String) : UiEvent
//    data class Snackbar(val message: String, val type: SnackbarType = SnackbarType.SUCCESS) :
//        UiEvent
//}
//
//enum class SnackbarType { SUCCESS, ERROR, WARNING, INFO }
//
//sealed interface UiEffect {
//    data class Toast(
//        val message: String,
//        val type: SnackbarType = SnackbarType.INFO
//    ) : UiEffect
//}
//
//// ============================================
//// BASE VIEWMODEL (Pure - No Network, No Compose)
//// ============================================
//
//// ============================================
//// STATE HANDLER
//// ============================================
//class StateHandler<STATE, R, I>(
//    private val getState: () -> STATE?,
//    private val setState: (STATE.() -> STATE) -> Unit
//) where STATE : BaseState<R, I, STATE> {
//
//    fun getItems(): List<I> = getState()?.items ?: emptyList()
//    fun getRequest(): R? = getState()?.request
//    fun getItem(): I? = getState()?.item
//
//    fun updateItems(update: List<I>.() -> List<I>) {
//        setState { copies(items = items.update()) }
//    }
//
//    fun updateItem(update: I.() -> I?) {
//        setState { copies(item = item?.update()) }
//    }
//
//    fun updateRequest(update: R.() -> R) {
//        setState { copies(request = request?.update()) }
//    }
//}
