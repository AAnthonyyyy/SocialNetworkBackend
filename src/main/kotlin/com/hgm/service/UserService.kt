package com.hgm.service

import com.hgm.data.models.User
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.requests.CreateAccountRequest
import com.hgm.data.requests.LoginRequest

class UserService(
    private val repository: UserRepository
) {

    private suspend fun doesUserExist(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    suspend fun getUserByEmail(email: String): User? {
        return repository.getUserByEmail(email)
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
        repository.createUser(
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

    suspend fun doesPasswordMatchForUser(request: LoginRequest): Boolean {
        return repository.doesPasswordMatchForUser(
            email = request.email,
            enterPassword = request.password
        )
    }

    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return repository.doesEmailBelongToUserId(email, userId)
    }


    sealed class ValidationEvent {
        object UserExist : ValidationEvent()
        object FieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}