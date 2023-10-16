package com.hgm.data.responses

data class AuthResponse(
    val successful: Boolean,
    val message: String? = null,
    val token: String
)
