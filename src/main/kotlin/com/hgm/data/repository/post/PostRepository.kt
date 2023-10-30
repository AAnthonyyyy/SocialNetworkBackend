package com.hgm.data.repository.post

import com.hgm.data.model.Post
import com.hgm.data.responses.PostResponse
import com.hgm.utils.Constants.DEFAULT_POST_PAGE_SIZE
import com.hgm.utils.Constants.DEFAULT_POST_PAGE

interface PostRepository {

    suspend fun createPost(post: Post):Boolean

    suspend fun deletePost(postId: String)

    suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int = DEFAULT_POST_PAGE,
        pageSize: Int = DEFAULT_POST_PAGE_SIZE
    ): List<PostResponse>

    suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int = DEFAULT_POST_PAGE,
        pageSize: Int = DEFAULT_POST_PAGE_SIZE
    ): List<PostResponse>

    suspend fun getPost(postId: String): Post?

    suspend fun getPostDetails(userId: String, postId: String): PostResponse?

}