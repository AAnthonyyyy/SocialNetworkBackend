package com.hgm.data.repository.like

interface LikeRepository {

    suspend fun likePost(userId: String, parentId: String): Boolean
    suspend fun unlikePost(userId: String, parentId: String): Boolean
    suspend fun removeLikeByParent(parentId: String)
}