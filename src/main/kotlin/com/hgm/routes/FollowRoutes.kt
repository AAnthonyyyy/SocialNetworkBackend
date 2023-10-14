package com.hgm.routes

import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.requests.FollowRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.util.ApiMessage
import com.hgm.util.ApiMessage.FOLLOWING_SUCCESSFUL
import com.hgm.util.ApiMessage.UNFOLLOWING_SUCCESSFUL
import com.hgm.util.ApiMessage.USER_NOT_FOUND
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.followUser(followRepository: FollowRepository) {
    post("/api/following/follow") {
        val request = call.receiveOrNull<FollowRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExist = followRepository.followUser(
            request.followingUserId,
            request.followedUserId
        )
        if (didUserExist) {
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


fun Route.unfollowUser(followRepository: FollowRepository) {
    delete("/api/following/unfollow") {
        val request = call.receiveOrNull<FollowRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val didUserExist = followRepository.unFollowUser(
            request.followingUserId,
            request.followedUserId
        )
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