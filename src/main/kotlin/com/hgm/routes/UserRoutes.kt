package com.hgm.routes

import com.google.gson.Gson
import com.hgm.data.requests.UpdateProfileRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.data.responses.UserItemResponse
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.utils.*
import com.hgm.utils.Constants.BASE_URL
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File


/** 查询用户 */
fun Route.searchUser(
    userService: UserService
) {
    authenticate {
        get("/api/user/query") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if (query.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.OK,
                    listOf<UserItemResponse>()
                )
                return@get
            }

            val searchResult = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchResult
            )
        }
    }
}


/** 获取个人信息 */
fun Route.getUserProfile(
    userService: UserService
) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            if (userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val profile = userService.getUserProfile(userId, call.userId) ?: kotlin.run {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessage.USER_NOT_FOUND
                    )
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    data = profile
                )
            )
        }
    }
}


/** 更新个人信息 */
fun Route.updateUserProfile(
    userService: UserService
) {
    val gson: Gson by inject()
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null
            var profilePictureFileName: String? = null
            var bannerPictureFileName: String? = null

            //多部分上传，按照类型分开处理（表单 or 图片）
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(partData.value, UpdateProfileRequest::class.java)
                        }
                    }

                    is PartData.FileItem -> {
                        if (partData.name == "profile_picture") {
                            profilePictureFileName = partData.save(Constants.PROFILE_PICTURE_PATH)
                        } else if (partData.name == "banner_picture") {
                            bannerPictureFileName = partData.save(Constants.BANNER_PICTURE_PATH)
                        }
                    }

                    is PartData.BinaryItem -> Unit
                }
            }

            //头像路径
            val profilePictureUrl = "${BASE_URL}profile_pictures/$profilePictureFileName"
            //背景图路劲
            val bannerPictureUrl = "${BASE_URL}banner_pictures/$bannerPictureFileName"

            updateProfileRequest?.let { request ->
                val updateAcknowledge = userService.updateUser(
                    userId = call.userId,
                    profilePictureUrl = if (profilePictureFileName == null) null else profilePictureUrl,
                    bannerPictureUrl = if (bannerPictureFileName == null) null else bannerPictureUrl,
                    request = request
                )
                if (updateAcknowledge) {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse<Unit>(
                            successful = true,
                            message = ApiResponseMessage.PROFILE_UPDATE_SUCCESSFUL
                        )
                    )
                } else {
                    //更新失败的话需要把上传的照片资源删除掉
                    File("src/main/${Constants.PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}


/** 获取个人发布的帖子 */
fun Route.getPostsForProfile(
    postService: PostService,
) {
    authenticate {
        get("/api/user/post") {
            //.......
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: call.userId
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE


            val posts = postService.getPostsForProfile(
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