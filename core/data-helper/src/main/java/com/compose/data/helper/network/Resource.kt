package com.compose.data.helper.network

import com.compose.data.helper.utils.ext.def

data class Resource<out T>(
    val state: State,
    val body: T? = null,
    val message: String = "Server Error",
    val errorCode: String? = null,
    val total: Int = 0,
    val lastPage: Int = 0,
    val lastSync: String? = null
) {

    companion object {

        fun <T> success(
            data: T?,
            message: String = "Server Error",
            lastPage: Int = 1,
            total: Int? = 0,
            lastSync: String? = null
        ): Resource<T> {
            return Resource(
                state = State.SUCCESS,
                body = data,
                message = message,
                lastPage = lastPage,
                total = total.def(),
                lastSync = lastSync
            )
        }

        fun <T> error(msg: String?, errorCode: String?): Resource<T> {
            return Resource(
                state = State.ERROR,
                message = msg ?: "Server Error",
                errorCode = errorCode ?: "ERROR"
            )
        }

        fun <T> loading(): Resource<T> {
            return Resource(State.LOADING, body = null)
        }

    }
}