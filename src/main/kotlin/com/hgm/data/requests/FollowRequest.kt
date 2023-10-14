package com.hgm.data.requests

data class FollowRequest(
    val followingUserId: String,
    val followedUserId: String,
)