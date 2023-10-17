package com.hgm.service

import com.hgm.data.repository.like.LikeRepository
import com.hgm.data.utils.ParentType

class LikeService(
    private val repository: LikeRepository
) {
    suspend fun likePost(userId: String, parentId: String, parentType: Int): Boolean {
        return repository.likePost(userId, parentId, parentType)
    }

    suspend fun unlikePost(userId: String, parentId: String): Boolean {
        return repository.unlikePost(userId, parentId)
    }

    suspend fun removeLikeForParent(parentId: String) {
        repository.removeLikeForParent(parentId)
    }

}