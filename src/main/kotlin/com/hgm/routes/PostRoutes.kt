package com.hgm.routes

import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.utils.ApiMessage
import com.hgm.utils.Constants
import com.hgm.utils.QueryParams
import com.hgm.utils.ifEmailBelongsToUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPost(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ){
                val doesUserExist = postService.createPost(request)
                if (!doesUserExist) {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = false,
                            message = ApiMessage.USER_NOT_FOUND
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = true,
                            message = ApiMessage.CREATE_POST_SUCCESSFUL
                        )
                    )
                }
            }
        }
    }
}


fun Route.getPostsFromFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get("/api/post/") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            ifEmailBelongsToUser(
                userId = userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ) {
                val posts = postService.getPostsFromFollows(userId, page, pageSize)
            }
        }
    }
}

