package com.hgm.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.hgm.data.requests.CreateAccountRequest
import com.hgm.data.requests.LoginRequest
import com.hgm.data.responses.AuthResponse
import com.hgm.data.responses.BaseResponse
import com.hgm.service.UserService
import com.hgm.service.UserService.ValidationEvent.Success
import com.hgm.service.UserService.ValidationEvent.FieldEmpty
import com.hgm.service.UserService.ValidationEvent.UserExist
import com.hgm.util.ApiMessage.EMAIL_ALREADY_EXIST
import com.hgm.util.ApiMessage.FIELDS_BLANK
import com.hgm.util.ApiMessage.LOGIN_SUCCESSFUL
import com.hgm.util.ApiMessage.LOGIN_FAILED
import com.hgm.util.ApiMessage.REGISTER_SUCCESSFUL
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

/** 注册 */
fun Route.registerUser(
    userService: UserService
) {
    post("/api/user/register") {
        // 接收json请求并转换成对应的数据类型
        val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        // 验证创建请求
        when (userService.validateCreateRequest(request)) {
            is UserExist -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    BaseResponse(
                        successful = false,
                        message = EMAIL_ALREADY_EXIST
                    )
                )
                return@post
            }

            is FieldEmpty -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    BaseResponse(
                        successful = false,
                        message = FIELDS_BLANK
                    )
                )
                return@post
            }

            is Success -> {
                userService.createAccount(request)
                call.respond(
                    BaseResponse(
                        successful = true,
                        message = REGISTER_SUCCESSFUL
                    )
                )
            }
        }
    }
}


/** 登录 */
fun Route.loginUser(
    userService: UserService,
    jwtAudience: String,
    jwtIssuer: String,
    jwtSecret: String
) {
    post("/api/user/login") {
        val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val doesPasswordMatch = userService.doesPasswordMatchForUser(request)
        if (doesPasswordMatch) {
            // 生成Token
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withClaim("email", request.email)
                .sign(Algorithm.HMAC256(jwtSecret))

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(token = token)
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = false,
                    message = LOGIN_FAILED
                )
            )
        }
    }
}