package com.hgm.service

import com.hgm.data.repository.follow.FollowRepository

class FollowService(
    private val repository: FollowRepository
) {

    suspend fun followUserIfExist(followingUserId:String,followedUserId:String):Boolean{
        return repository.followUserIfExist(
            followingUserId,
            followedUserId
        )
    }

    suspend fun unfollowUserIfExist(followingUserId:String,followedUserId:String):Boolean{
        return repository.unFollowUserIfExist(
            followingUserId,
            followedUserId
        )
    }
}