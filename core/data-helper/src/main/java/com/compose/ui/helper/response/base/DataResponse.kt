package com.compose.ui.helper.response.base

data class DataResponse<T>(
    override var message: String = "",
    override var last_page: Int = 0,
    var code: String = "",
    override var data: T? = null,
    override val total: Int?,
    override val lastSync: String?
) : BaseResponse<T>