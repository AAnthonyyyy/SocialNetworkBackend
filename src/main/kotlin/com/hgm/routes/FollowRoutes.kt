package com.hgm.routes

import com.hgm.data.requests.FollowRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.FollowService
import com.hgm.utils.ApiMessage.FOLLOWING_SUCCESSFUL
import com.hgm.utils.ApiMessage.UNFOLLOWING_SUCCESSFUL
import com.hgm.utils.ApiMessage.USER_NOT_FOUND
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.followUser(followService: FollowService) {
    post("/api/following/follow") {
        val request = call.receiveOrNull<FollowRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val doesUserExist = followService.followUserIfExist(request)
        if (doesUserExist) {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    message = FOLLOWING_SUCCESSFUL
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}


fun Route.unfollowUser(followService: FollowService) {
    delete("/api/following/unfollow") {
        val request = call.receiveOrNull<FollowRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val didUserExist = followService.unfollowUserIfExist(request)
        if (didUserExist) {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    message = UNFOLLOWING_SUCCESSFUL
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}