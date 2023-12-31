package com.hgm.routes

import com.hgm.data.requests.AddCommentRequest
import com.hgm.data.requests.DeleteCommentRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.ActivityService
import com.hgm.service.CommentService
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

fun Route.addComment(
    commentService: CommentService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/comment/add") {
            val request = call.receiveOrNull<AddCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            when (commentService.addComment(request, call.userId)) {
                is CommentService.ValidationEvent.CommentTooLong -> {
                    call.respond(
                        BaseResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessage.COMMENT_LENGTH_TOO_LONG
                        )
                    )
                }

                is CommentService.ValidationEvent.FieldEmpty -> {
                    call.respond(
                        BaseResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessage.FIELDS_BLANK
                        )
                    )
                }

                is CommentService.ValidationEvent.Success -> {
                    activityService.createCommentActivity(
                        byUserId = call.userId,
                        postId = request.postId,
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse<Unit>(
                            successful = true,
                            message = ApiResponseMessage.ADD_COMMENT_SUCCESSFUL
                        )
                    )
                }

                is CommentService.ValidationEvent.UserNotFound -> {
                    call.respond(
                        BaseResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessage.USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}


fun Route.getCommentForPost(
    commentService: CommentService,
) {
    //authenticate {
    //    get("/api/comment/get") {
    //        val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
    //            call.respond(HttpStatusCode.BadRequest)
    //            return@get
    //        }
    //        val comments = commentService.getCommentForPost(postId,call.userId)
    //        call.respond(HttpStatusCode.OK, comments)
    //    }
    //}
    get("/api/comment/get") {
        val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val comments = commentService.getCommentForPost(postId,call.userId)
        call.respond(HttpStatusCode.OK, comments)
    }
}


fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
) {
    authenticate {
        delete("/api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val comment = commentService.getCommentById(request.commentId) ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            if (comment.userId != call.userId) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val deleteSuccessful = commentService.deleteComment(request.commentId)
            if (deleteSuccessful) {
                likeService.removeLikeForParent(request.commentId)
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = true,
                        message = ApiResponseMessage.DELETE_COMMENT_SUCCESSFUL
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    BaseResponse<Unit>(
                        successful = false,
                    )
                )
            }
        }
    }
}