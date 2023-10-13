package com.hgm.data.repository.user

import com.hgm.data.models.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun doesPasswordForUserMatch(
        email: String,
        enterPassword: String
    ): Boolean
}