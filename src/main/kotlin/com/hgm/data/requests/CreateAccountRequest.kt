package com.hgm.data.requests

/**
 * 用于接收用户发起注册的请求
 */
data class CreateAccountRequest(
    val email: String,
    val username: String,
    val password: String
)
