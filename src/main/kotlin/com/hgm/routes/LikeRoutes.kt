package com.hgm.routes

import com.hgm.data.requests.LikeUpdateRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.data.utils.ParentType
import com.hgm.service.ActivityService
import com.hgm.service.LikeService
import com.hgm.utils.ApiResponseMessage
import com.hgm.utils.QueryParams
import com.hgm.utils.userId
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/like/likes") {
            val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val likeSuccessful =
                likeService.likeParent(call.userId, request.parentId, request.parentType)
            if (likeSuccessful) {
                activityService.createLikeActivity(
                    byUserId = call.userId,
                    parentType = ParentType.fromType(request.parentType),
                    parentId = request.parentId
                )
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = true,
                        message = ApiResponseMessage.LIKE_POST_SUCCESSFUL
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessage.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}


fun Route.unlikeParent(
    likeService: LikeService,
) {
    authenticate {
        delete("/api/like/unlike") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val parentType = call.parameters[QueryParams.PARAM_PARENT_TYPE]?.toIntOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val unlikeSuccessful = likeService.unlikeParent(call.userId, parentId, parentType)
            if(unlikeSuccessful) {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = true
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessage.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}

fun Route.getLikesForParent(
    likeService: LikeService
) {
    authenticate {
        get("/api/like/parent") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }


            val usersWhoLikedParent = likeService.getUsersWhoLikesParent(
                parentId = parentId,
                userId = call.userId
            )
            call.respond(
                HttpStatusCode.OK,
                usersWhoLikedParent
            )
        }
    }
}