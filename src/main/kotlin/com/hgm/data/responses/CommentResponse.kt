package com.hgm.data.responses

data class CommentResponse(
    val id: String,
    val profilePictureUrl: String,
    val username: String,
    val timestamp: Long,
    val comment: String,
    val likeCount: Int,
    val isLiked: Boolean
)
