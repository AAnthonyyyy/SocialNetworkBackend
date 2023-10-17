package com.hgm.service

import com.hgm.data.models.Post
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.requests.CreatePostRequest
import com.hgm.utils.Constants

class PostService(
    private val repository: PostRepository
) {

    suspend fun createPost(request: CreatePostRequest,userId: String): Boolean {
        return repository.createPost(
            Post(
                userId = userId,
                imageUrl = "",
                description = request.description,
                timestamp = System.currentTimeMillis()
            )
        )
    }


    suspend fun getPostsForFollows(
        userId: String,
        page: Int = Constants.DEFAULT_POST_PAGE,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostsByFollows(userId, page, pageSize)
    }

    suspend fun getPostsForProfile(
        userId: String,
        page: Int = Constants.DEFAULT_POST_PAGE,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostsForProfile(userId, page, pageSize)
    }


    suspend fun getPost(postId: String): Post?{
        return repository.getPost(postId)
    }

    suspend fun deletePost(postId: String){
        repository.deletePost(postId)
    }
}