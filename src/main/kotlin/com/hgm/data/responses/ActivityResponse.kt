package com.hgm.data.responses

data class ActivityResponse(
    val timestamp: Long,
    val userId: String,
    val username: String,
    val parentId: String,
    val type: Int,
    val id: String
)
