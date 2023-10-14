package com.hgm.service

import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.requests.FollowRequest

class FollowService(
    private val repository: FollowRepository
) {

    suspend fun followUserIfExist(request:FollowRequest):Boolean{
        return repository.followUserIfExist(
            request.followingUserId,
            request.followedUserId
        )
    }

    suspend fun unfollowUserIfExist(request:FollowRequest):Boolean{
        return repository.unFollowUserIfExist(
            request.followingUserId,
            request.followedUserId
        )
    }
}