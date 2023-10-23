package com.hgm.repository.user

import com.hgm.data.model.User
import com.hgm.data.repository.user.UserRepository

/**
 * 模拟真实环境，编写测试用例
 */
class FakeUserRepository : UserRepository {

    val users= mutableListOf<User>()

    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.find { it.id == id }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun doesPasswordMatchForUser(email: String, enterPassword: String): Boolean {
        val user=getUserByEmail(email)
        return user?.password==enterPassword
    }
}