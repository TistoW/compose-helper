package com.tisto.helper.core.helper.retrofit.response.base

data class DataResponseRetrofit<T>(
    override var message: String = "",
    override var last_page: Int = 0,
    var code: String = "",
    override var data: T? = null,
    override val total: Int?,
    override val lastSync: String?
) : BaseResponseRetrofit<T>