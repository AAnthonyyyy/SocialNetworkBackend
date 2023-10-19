package com.hgm.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.hgm.data.requests.CreateAccountRequest
import com.hgm.data.requests.LoginRequest
import com.hgm.data.responses.AuthResponse
import com.hgm.data.responses.BaseResponse
import com.hgm.service.UserService
import com.hgm.utils.ApiResponseMessage
import com.hgm.utils.Constants
import io.ktor.application.*
import io.ktor.auth.*
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
            is UserService.ValidationEvent.UserExist -> {
                call.respond(
                    BaseResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessage.EMAIL_ALREADY_EXIST
                    )
                )
                return@post
            }

            is UserService.ValidationEvent.FieldEmpty -> {
                call.respond(
                    BaseResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessage.FIELDS_BLANK
                    )
                )
                return@post
            }

            is UserService.ValidationEvent.Success -> {
                userService.createAccount(request)
                call.respond(
                    BaseResponse<Unit>(
                        successful = true,
                        message = ApiResponseMessage.REGISTER_SUCCESSFUL
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

        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(
                    successful = false,
                    message = ApiResponseMessage.LOGIN_FAILED
                )
            )
            return@post
        }


        val doesPasswordMatch = userService.isValidPassword(
            enterPassword = request.password,
            actualPassword = user.password
        )
        if (doesPasswordMatch) {
            // 生成Token
            val expiresIn = 1000L * 60L * 60L * 24L * 365L //过期时间为一年
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withClaim(Constants.KEY_CLAIM_USER_ID, user.id)
                .sign(Algorithm.HMAC256(jwtSecret))

            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    message = ApiResponseMessage.LOGIN_SUCCESSFUL,
                    data = AuthResponse(token = token)
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse<Unit>(
                    successful = false,
                    message = ApiResponseMessage.LOGIN_FAILED
                )
            )
        }
    }
}


/** 验证token是否过期 */
fun Route.authenticate() {
    authenticate {
        get("/api/user/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}
