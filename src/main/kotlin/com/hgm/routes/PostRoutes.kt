package com.hgm.routes

import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.util.ApiMessage.CREATE_POST_SUCCESSFUL
import com.hgm.util.ApiMessage.USER_NOT_FOUND
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

            // 验证邮箱和用户ID是否匹配
            val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class) ?: ""
            val isEmailByUser = userService.doesEmailBelongToUserId(email, request.userId)
            if (!isEmailByUser){
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "您的身份存在疑问，无法进行发帖操作"
                )
                return@post
            }


            val doesUserExist = postService.createPost(request)
            if (!doesUserExist) {
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
}