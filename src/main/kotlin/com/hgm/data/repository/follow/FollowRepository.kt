package com.hgm.data.repository.follow

import com.hgm.data.model.Following

interface FollowRepository {

    suspend fun followUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun unFollowUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun checkUserFollowing(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun getFollowsByUser(userId: String): List<Following>
}