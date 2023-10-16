package com.hgm.data.requests

data class AddCommentRequest(
    val postId: String,
    val comment: String
)
