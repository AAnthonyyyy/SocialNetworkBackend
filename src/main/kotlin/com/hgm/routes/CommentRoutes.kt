package com.hgm.routes

import com.hgm.data.requests.AddCommentRequest
import com.hgm.data.requests.DeleteCommentRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.CommentService
import com.hgm.service.LikeService
import com.hgm.service.UserService
import com.hgm.utils.ApiResponseMessage
import com.hgm.utils.QueryParams
import com.hgm.utils.ifEmailBelongsToUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.addComment(
    commentService: CommentService,
    userService: UserService
) {
    authenticate {
        post("/api/comment/add") {
            val request = call.receiveOrNull<AddCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ) {
                when (commentService.addComment(request)) {
                    CommentService.ValidationEvent.CommentTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BaseResponse(
                                successful = false,
                                message = ApiResponseMessage.COMMENT_LENGTH_TOO_LONG
                            )
                        )
                    }

                    CommentService.ValidationEvent.FieldEmpty -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BaseResponse(
                                successful = false,
                                message = ApiResponseMessage.FIELDS_BLANK
                            )
                        )
                    }

                    CommentService.ValidationEvent.Success -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BaseResponse(
                                successful = true,
                                message = ApiResponseMessage.ADD_COMMENT_SUCCESSFUL
                            )
                        )
                    }
                }
            }
        }
    }
}


fun Route.getCommentByPost(
    commentService: CommentService,
) {
    authenticate {
        get("/api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val comments = commentService.getCommentByPost(postId)
            call.respond(HttpStatusCode.OK,comments)
        }
    }
}


fun Route.deleteComment(
    commentService: CommentService,
    userService: UserService,
    likeService: LikeService
){
    authenticate {
        delete("/api/comment/delete"){
            val request=call.receiveOrNull<DeleteCommentRequest>()?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ){
                val deleteSuccessful = commentService.deleteComment(request.commentId)
                if (deleteSuccessful){
                    likeService.removeLikeByParent(request.commentId)
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = true,
                            message = ApiResponseMessage.DELETE_COMMENT_SUCCESSFUL
                        )
                    )
                }else{
                    call.respond(
                        HttpStatusCode.NotFound,
                        BaseResponse(
                            successful = false,
                        )
                    )
                }
            }
        }
    }
}