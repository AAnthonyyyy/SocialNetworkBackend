package com.hgm.data.requests

data class AddCommentRequest(
    val userId: String,
    val postId: String,
    val comment: String
)
