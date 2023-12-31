package com.hgm.routes

import com.hgm.data.model.Activity
import com.hgm.data.requests.FollowUpdateRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.data.utils.ActivityType
import com.hgm.service.ActivityService
import com.hgm.service.FollowService
import com.hgm.utils.ApiResponseMessage.FOLLOWING_SUCCESSFUL
import com.hgm.utils.ApiResponseMessage.UNFOLLOWING_SUCCESSFUL
import com.hgm.utils.ApiResponseMessage.USER_NOT_FOUND
import com.hgm.utils.QueryParams
import com.hgm.utils.userId
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.followUser(
    followService: FollowService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/following/follow") {
            val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val doesUserExist = followService.followUserIfExist(call.userId, request.followedUserId)
            if (doesUserExist) {
                activityService.createLikeActivity(
                    Activity(
                        byUserId = call.userId,
                        toUserId = request.followedUserId,
                        type = ActivityType.FollowedYou.type,
                        timestamp = System.currentTimeMillis(),
                        parentId = ""
                    )
                )
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = true,
                        message = FOLLOWING_SUCCESSFUL
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}





fun Route.unfollowUser(
    followService: FollowService
) {
    authenticate {
        delete("/api/following/unfollow") {
            val followedUserId = call.parameters[QueryParams.PARAM_FOLLOWED_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val didUserExist = followService.unfollowUserIfExist(call.userId,followedUserId)
            if (didUserExist) {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = true,
                        message = UNFOLLOWING_SUCCESSFUL
                    )
                )
            } else {
                call.respond(
                    BaseResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}