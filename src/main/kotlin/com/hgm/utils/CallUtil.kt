package com.hgm.utils

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*


/**
 * 扩展JWTPrincipal，从声明中提取用户ID作为属性提供外部使用
 */
val JWTPrincipal.userId: String?
    get() = getClaim(Constants.KEY_CLAIM_USER_ID, String::class)


/**
 * 扩展ApplicationCall，使用call直接访问JWTPrincipal中的用户ID
 */
val ApplicationCall.userId: String
    get() = principal<JWTPrincipal>()?.userId.toString()


/**
 * 验证用户
 * 提取token中的邮箱和请求中的用户ID，然后使用用户ID查询数据库
 * 将token中的邮箱和查询出来的邮箱进行对比即可。
 */
//suspend fun PipelineContext<Unit, ApplicationCall>.ifEmailBelongsToUser(
//    userId: String,
//    onValidateEmail: suspend (email: String, userId: String) -> Boolean,
//    onSuccess: suspend () -> Unit
//) {
//    // 获取匹配结果
//    val matchResult = onValidateEmail(
//        call.principal<JWTPrincipal>()?.email ?: "",
//        userId
//    )
//
//    if (matchResult) {
//        onSuccess()
//    } else {
//        call.respond(
//            HttpStatusCode.Unauthorized,
//            BaseResponse(
//                successful = false,
//                message = ApiResponseMessage.CREATE_POST_AUTH_ERROR
//            )
//        )
//    }
//}