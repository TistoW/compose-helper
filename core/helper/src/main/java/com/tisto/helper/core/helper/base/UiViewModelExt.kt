package com.tisto.helper.core.helper.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tisto.helper.core.helper.component.showError
import com.tisto.helper.core.helper.component.showInfo
import com.tisto.helper.core.helper.component.showSuccess
import com.tisto.helper.core.helper.component.showWarning
import com.tisto.kmp.helper.utils.model.FilterItem
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.SnackbarHostState

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

data class BaseUiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val isLoadingProcess: Boolean = false,
    val isRefreshing: Boolean = false,
    val screen: String = ScreenTypes.list,
    val search: String? = null,
    val filters: List<FilterItem> = emptyList(),
    val successMessage: String? = null,
    val backAction: String? = null,

    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val formScrollState: ScrollState = ScrollState(0),
    val listScrollState: LazyListState = LazyListState(),
    val gridScrollState: LazyGridState = LazyGridState(), // ✅ NEW

    // Pagination
    val page: Int = 1,
    val pageLimit: Int = 10,
    val totalPage: Int = 1,
    val loadingSize: Int = 0,
    val loadedCount: Int = 0,
    val totalSize: Int = 0,
    val hasMore: Boolean = false,
) {
    val isSearching: Boolean
        get() = !search.isNullOrEmpty()
    val isEmpty: Boolean
        get() = loadingSize == 0 && loadedCount == 0
}