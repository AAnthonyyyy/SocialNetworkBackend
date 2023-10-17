package com.hgm.data.responses

data class UserResponseItem(
    val username: String,
    val profilePictureUrl: String,
    val bio: String,
    val isFollowing: Boolean
)