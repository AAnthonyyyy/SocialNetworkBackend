package com.hgm.service

import com.hgm.data.model.Post
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.responses.PostResponse
import com.hgm.utils.Constants

class PostService(
    private val repository: PostRepository
) {

    suspend fun createPost(
        request: CreatePostRequest,
        userId: String,
        imageUrl: String
    ): Boolean {
        return repository.createPost(
            Post(
                userId = userId,
                imageUrl = imageUrl,
                description = request.description,
                timestamp = System.currentTimeMillis()
            )
        )
    }


    suspend fun getPostsForFollows(
        ownUserId: String,
        page: Int = Constants.DEFAULT_POST_PAGE,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<PostResponse> {
        return repository.getPostsByFollows(ownUserId, page, pageSize)
    }

    suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int = Constants.DEFAULT_POST_PAGE,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<PostResponse> {
        return repository.getPostsForProfile(ownUserId,userId, page, pageSize)
    }


    suspend fun getPost(postId: String): Post? {
        return repository.getPost(postId)
    }

    suspend fun getPostDetails(ownUserId: String, postId: String): PostResponse? {
        return repository.getPostDetails(ownUserId, postId)
    }

    suspend fun deletePost(postId: String) {
        repository.deletePost(postId)
    }
}