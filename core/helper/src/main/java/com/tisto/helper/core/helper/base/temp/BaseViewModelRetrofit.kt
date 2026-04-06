package com.tisto.helper.core.helper.base.temp

import com.tisto.helper.core.helper.base.BaseState
import com.tisto.helper.core.helper.retrofit.base.BaseRetrofitViewModel
import com.tisto.helper.core.helper.retrofit.network.ResourceRetrofit

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