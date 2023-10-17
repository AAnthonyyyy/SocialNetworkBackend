package com.hgm.service

import com.hgm.data.models.User
import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.requests.CreateAccountRequest
import com.hgm.data.requests.UpdateProfileRequest
import com.hgm.data.responses.ProfileResponse
import com.hgm.data.responses.UserResponseItem

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    private suspend fun doesUserExist(email: String): Boolean {
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun getUserProfile(userId: String, callUserId: String): ProfileResponse? {
        //查询该用户的信息
        val user = userRepository.getUserById(userId) ?: return null
        return ProfileResponse(
            username = user.username,
            profilePictureUrl = user.profileImageUrl,
            bio = user.bio,
            followingCount = user.followingCount,
            followedCount = user.followedCount,
            postCount = user.postCount,
            topSkillUrls = user.skills,
            githubUrl = user.githubUrl,
            instagramUrl = user.instagramUrl,
            linkedInUrl = user.linkedInUrl,
            isOwnProfile = userId == callUserId,
            isFollowing = if (userId != callUserId) {
                followRepository.checkUserFollowing(callUserId, userId)
            } else false
        )
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    fun isValidPassword(enterPassword: String, actualPassword: String): Boolean {
        return enterPassword == actualPassword
    }

    suspend fun validateCreateRequest(request: CreateAccountRequest): ValidationEvent {
        return if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            ValidationEvent.FieldEmpty
        } else if (doesUserExist(request.email)) {
            ValidationEvent.UserExist
        } else {
            ValidationEvent.Success
        }
    }

    suspend fun createAccount(request: CreateAccountRequest) {
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                githubUrl = "",
                instagramUrl = "",
                linkedInUrl = ""
            )
        )
    }

    suspend fun updateUser(
        userId: String,
        profilePictureUrl: String,
        request: UpdateProfileRequest
    ): Boolean {
        return userRepository.updateUser(userId, profilePictureUrl, request)
    }


    suspend fun searchForUsers(query: String, userId: String): List<UserResponseItem> {
        //获取当前用户的所有关注人信息
        val followsByUser = followRepository.getFollowsByUser(userId)

        return userRepository.searchForUser(query).map { user ->
            //检查关注人列表是否包含我们查询的用户
            val isFollowing = followsByUser.find {
                it.followedUserId == user.id
            } != null

            UserResponseItem(
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
    }

    sealed class ValidationEvent {
        object UserExist : ValidationEvent()
        object FieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}