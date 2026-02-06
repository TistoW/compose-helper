package com.tisto.helper.core.helper.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.tisto.helper.core.helper.retrofit.network.Resource
import com.tisto.helper.core.helper.retrofit.network.State
import com.tisto.helper.core.helper.retrofit.response.base.ErrorResponse

fun <T> LiveData<Resource<T>>.observer(
    lifecycleOwner: LifecycleOwner,
    onError: (ErrorResponse) -> Unit = {},
    onErrorAllResponse: ((Resource<T>) -> Unit) = { },
    onLoading: () -> Unit = { },
    onFinished: () -> Unit = { },
    onSuccessAllResponse: (Resource<T>) -> Unit = {},
    onSuccess: (T) -> Unit = {}
) {
    observe(lifecycleOwner) {
        when (it.state) {
            State.SUCCESS -> {
                onSuccessAllResponse(it)
                if (it.body != null) {
                    onSuccess(it.body)
                } else onError(
                    ErrorResponse(
                        message = "Data is null", errorCode = it.errorCode
                    )
                )
                onFinished()
            }

            State.ERROR -> {
                val error = ErrorResponse(
                    message = it.message, errorCode = it.errorCode
                )
                onError(error)
                onErrorAllResponse.invoke(it)
                onFinished()
            }

            State.LOADING -> {
                onLoading()
            }
        }
    }
}