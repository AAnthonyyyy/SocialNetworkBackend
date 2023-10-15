package com.hgm.service

import com.hgm.data.models.Post
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.requests.CreatePostRequest
import com.hgm.utils.Constants

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


    suspend fun getPostsFromFollows(
        userId: String,
        page: Int = Constants.DEFAULT_POST_PAGE,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ):List<Post>{
        return repository.getPostsFromFollows(userId, page, pageSize)
    }
}