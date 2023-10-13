package com.hgm.routes

import com.hgm.data.repository.user.UserRepository
import com.hgm.data.models.User
import com.hgm.data.requests.RegisterAccountRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.util.ApiResponse.FIELDS_BLANK
import com.hgm.util.ApiResponse.REGISTER_SUCCESSFUL
import com.hgm.util.ApiResponse.USER_ALREADY_EXIST
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.userRoutes(
    userRepository: UserRepository
) {
    post("/api/user/register") {
        //接收json请求并转换成对应的数据类型
        val request = call.receiveOrNull<RegisterAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        //检查工作
        if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                BaseResponse(
                    successful = false,
                    message = FIELDS_BLANK
                )
            )
            return@post
        }

        //验证用户是否已经存在
        val userExist = userRepository.getUserByEmail(request.email) != null
        if (userExist) {
            call.respond(
                BaseResponse(
                    successful = false,
                    message = USER_ALREADY_EXIST
                )
            )
            return@post
        }


        //验证成功
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                githubUrl = "",
                instagramUrl = "",
                linkedInUrl = ""
            )
        )
        call.respond(
            BaseResponse(
                successful = true,
                message = REGISTER_SUCCESSFUL
            )
        )
    }
}