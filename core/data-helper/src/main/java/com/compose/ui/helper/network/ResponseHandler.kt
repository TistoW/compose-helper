package com.compose.ui.helper.network

import com.compose.ui.helper.response.base.BaseResponse
import com.compose.ui.helper.utils.ext.def
import com.compose.ui.helper.utils.ext.getErrorBody
import com.compose.ui.helper.utils.ext.logs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import kotlin.let

fun <T, R> apiCall(
    apiCall: suspend () -> Response<out BaseResponse<T>>,
    onSuccess: ((R?) -> Unit)? = null,
    mapper: (T) -> R
): Flow<Resource<R>> = flow {
    emit(Resource.loading())
    val response = apiCall()
    if (response.isSuccessful) {
        val result = response.body()
        val data = result?.data?.let(mapper)
        onSuccess?.invoke(data)
        emit(
            Resource.success(
                data,
                result?.message ?: "Server Error",
                lastPage = result?.last_page.def(),
                lastSync = result?.lastSync,
                total = result?.total
            )
        )
    } else {
        val errorBody = response.getErrorBody()
        val errorCode = errorBody?.code
        emit(Resource.error(errorBody?.message ?: "Server Error", errorCode))
    }
}.catch { e ->
    logResponse(e.message)
    e.printStackTrace()
    emit(Resource.error(e.message.convertErrorMessage(), null))
}.flowOn(Dispatchers.IO)

fun logResponse(message: String?) {
    logs("executeApi : $message")
}

private fun String?.convertErrorMessage(): String {
    return when (this) {
        "unexpected end of stream" -> "Internal Server Error"
        null -> "Server Error"
        else -> "Server Error"
    }
}