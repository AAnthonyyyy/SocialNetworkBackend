package com.hgm.data.responses

/**
 * 用于响应请求，返回基础信息
 */
data class BaseResponse<T>(
    val successful: Boolean,
    val message: String? = null,
    val data: T? = null
)
