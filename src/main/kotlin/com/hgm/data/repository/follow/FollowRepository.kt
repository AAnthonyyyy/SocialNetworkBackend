package com.hgm.data.repository.follow

interface FollowRepository {

    suspend fun followUser(
        followingUserId: String,
        followedUserId: String
    ):Boolean

    suspend fun unFollowUser(
        followingUserId: String,
        followedUserId: String
    ):Boolean
}