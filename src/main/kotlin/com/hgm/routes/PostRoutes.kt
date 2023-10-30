package com.hgm.routes

import com.google.gson.Gson
import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.requests.DeletePostRequest
import com.hgm.data.requests.UpdateProfileRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.CommentService
import com.hgm.service.LikeService
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.utils.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.createPost(
    postService: PostService,
) {
    val gson by inject<Gson>()
    authenticate {
        post("/api/post/create") {
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null

            //多部分上传，按照类型分开处理（表单 or 图片）
            multipart.forEachPart { postData ->
                when (postData) {
                    is PartData.FormItem -> {
                        if (postData.name == "post_data") {
                            createPostRequest = gson.fromJson(
                                postData.value,
                                CreatePostRequest::class.java
                            )
                        }
                    }

                    is PartData.FileItem -> {
                        fileName = postData.save(Constants.POST_PICTURE_PATH)
                    }

                    is PartData.BinaryItem -> Unit
                }
            }

            //图片路径
            val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName"

            createPostRequest?.let { request ->
                val createPostAcknowledge = postService.createPost(
                    userId = call.userId,
                    imageUrl = postPictureUrl,
                    request = request
                )
                if (createPostAcknowledge) {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse<Unit>(
                            successful = true,
                            message = ApiResponseMessage.CREATE_POST_SUCCESSFUL
                        )
                    )
                } else {
                    //上传帖子失败的话需要把上传的照片资源删除掉
                    File("${Constants.POST_PICTURE_PATH}$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
        }
    }
}


fun Route.getPostsForFollows(
    postService: PostService,
) {
    authenticate {
        get("/api/post/get") {
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE


            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService,
) {
    authenticate {
        get("/api/user/post") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: call.userId
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE


            val posts = postService.getPostsForProfile(
                ownUserId = call.userId,
                userId = userId,
                page = page,
                pageSize = pageSize
            )
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}


fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
) {
    authenticate {
        delete("/api/post/delete") {
            val request = call.receiveOrNull<DeletePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val post = postService.getPost(request.postId) ?: kotlin.run {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            if (call.userId == post.userId) {
                //删除帖子也要把关于帖子的点赞以及评论删除
                postService.deletePost(request.postId)
                likeService.removeLikeForParent(request.postId)
                commentService.deleteCommentForPost(request.postId)
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = true,
                        message = ApiResponseMessage.DELETE_POST_SUCCESSFUL
                    )
                )
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}


fun Route.getPostDetail(
    postService: PostService,
) {
    authenticate {
        get("/api/post/detail") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val post = postService.getPostDetails(call.userId, postId) ?: kotlin.run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    data = post
                )
            )
        }
    }
}

