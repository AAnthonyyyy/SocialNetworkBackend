package com.hgm.data.repository.like

import com.hgm.data.models.Like
import com.hgm.utils.Constants

interface LikeRepository {

    suspend fun likePost(userId: String, parentId: String, parentType: Int): Boolean
    suspend fun unlikePost(userId: String, parentId: String): Boolean
    suspend fun removeLikeForParent(parentId: String)
    suspend fun getLikesForParent(
        parentId: String,
        page: Int = Constants.DEFAULT_LIKE_PAGE,
        pageSize: Int = Constants.DEFAULT_LIKE_PAGE_SIZE
    ): List<Like>
}