package com.hgm.routes

import com.hgm.data.models.Post
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.util.ApiMessage.CREATE_POST_SUCCESSFUL
import com.hgm.util.ApiMessage.USER_NOT_FOUND
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPost(postRepository: PostRepository) {
    post("/api/post/create") {
        val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExist = postRepository.createPost(
            Post(
                userId = request.userId,
                imageUrl = "",
                description = request.description,
                timestamp = System.currentTimeMillis()
            )
        )
        if (!didUserExist) {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    message = CREATE_POST_SUCCESSFUL
                )
            )
        }
    }
}