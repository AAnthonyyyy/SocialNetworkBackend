package com.hgm.service

import com.hgm.data.models.User
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.requests.CreateAccountRequest

class UserService(
    private val repository: UserRepository
) {

    private suspend fun doesUserExist(email: String): Boolean {
        return repository.getUserByEmail(email) != null
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


    sealed class ValidationEvent {
        object UserExist : ValidationEvent()
        object FieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}