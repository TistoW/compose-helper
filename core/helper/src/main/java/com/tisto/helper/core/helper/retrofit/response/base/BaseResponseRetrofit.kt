package com.tisto.helper.core.helper.retrofit.response.base

interface BaseResponseRetrofit<T> {
    val data: T?
    val message: String?
    val last_page: Int?
    val total: Int?
    val lastSync: String?
}
