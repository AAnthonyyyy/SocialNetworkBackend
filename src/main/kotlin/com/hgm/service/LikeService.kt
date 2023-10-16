package com.hgm.service

import com.hgm.data.repository.like.LikeRepository

class LikeService(
    private val repository: LikeRepository
) {
    suspend fun likePost(userId: String, parentId: String): Boolean {
        return repository.likePost(userId, parentId)
    }

    suspend fun unlikePost(userId: String, parentId: String): Boolean {
        return repository.unlikePost(userId, parentId)
    }

    suspend fun removeLikeByParent(parentId: String) {
        repository.removeLikeByParent(parentId)
    }

}