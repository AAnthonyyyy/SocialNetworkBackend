package com.hgm.service

import com.hgm.data.models.Post
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.requests.CreatePostRequest

class PostService(
    private val repository: PostRepository
) {

    suspend fun createPost(request:CreatePostRequest):Boolean{
        return repository.createPost(
            Post(
                userId = request.userId,
                imageUrl = "",
                description = request.description,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}