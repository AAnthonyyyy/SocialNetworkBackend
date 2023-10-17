package com.hgm.data.repository.user

import com.hgm.data.models.User
import com.hgm.data.requests.UpdateProfileRequest
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.litote.kmongo.regex

class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {

    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOneById(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun doesPasswordMatchForUser(
        email: String,
        enterPassword: String
    ): Boolean {
        val user = getUserByEmail(email)
        return user?.password == enterPassword
    }

    override suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return users.findOneById(userId)?.email == email
    }

    override suspend fun searchForUser(query: String): List<User> {
        return users.find(
            or(
                User::username regex Regex("(?i).*$query.*"),
                User::email eq query
            )
        ).toList()
    }

    override suspend fun updateUser(
        userId: String,
        profilePictureUrl: String?,
        request: UpdateProfileRequest
    ): Boolean {
        val user = users.findOneById(userId) ?: return false
        return users.updateOneById(
            id = userId,
            update = User(
                username = request.username,
                password = user.password,
                bio = request.bio,
                email = user.email,
                profileImageUrl = profilePictureUrl ?: user.profileImageUrl,
                githubUrl = request.githubUrl,
                instagramUrl = request.instagramUrl,
                linkedInUrl = request.linkedInUrl,
                skills = request.skills,
                followingCount = user.followingCount,
                followedCount = user.followedCount,
                postCount = user.postCount,
                id = user.id
            )
        ).wasAcknowledged()
    }
}