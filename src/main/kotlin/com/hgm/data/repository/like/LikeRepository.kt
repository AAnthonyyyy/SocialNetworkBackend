package com.hgm.data.repository.like

import com.hgm.data.utils.ParentType

interface LikeRepository {

    suspend fun likePost(userId: String, parentId: String,parentType: Int): Boolean
    suspend fun unlikePost(userId: String, parentId: String): Boolean
    suspend fun removeLikeForParent(parentId: String)
}