package com.hgm.data.repository.follow

interface FollowRepository {

    suspend fun followUserIfExist(
        followingUserId: String,
        followedUserId: String
    ):Boolean

    suspend fun unFollowUserIfExist(
        followingUserId: String,
        followedUserId: String
    ):Boolean
}