package com.compose.data.helper.response.base

interface BaseResponse<T> {
    val data: T?
    val message: String?
    val last_page: Int?
    val total: Int?
    val lastSync: String?
}
