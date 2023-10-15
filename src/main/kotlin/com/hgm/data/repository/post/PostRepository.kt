package com.hgm.data.repository.post

import com.hgm.data.models.Post
import com.hgm.util.Constants.DEFAULT_POST_PAGE_SIZE
import com.hgm.util.Constants.DEFAULT_POST_PAGE

interface PostRepository {

    suspend fun createPost(post: Post):Boolean

    suspend fun deletePost(postId: String)

    suspend fun getPostFromFollows(
        userId: String,
        page: Int = DEFAULT_POST_PAGE,
        pageSize: Int = DEFAULT_POST_PAGE_SIZE
    ): List<Post>
}