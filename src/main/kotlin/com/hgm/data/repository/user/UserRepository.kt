package com.hgm.data.repository.user

import com.hgm.data.models.User
import com.hgm.data.requests.UpdateProfileRequest

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun updateUser(userId: String, profilePictureUrl: String?, request: UpdateProfileRequest): Boolean

    suspend fun getUserByEmail(email: String): User?

    suspend fun doesPasswordMatchForUser(
        email: String,
        enterPassword: String
    ): Boolean


    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean

    suspend fun searchForUser(query: String): List<User>
}