package com.hgm.data.repository.like

import com.hgm.data.models.Like
import com.hgm.data.models.User
import com.hgm.data.utils.ParentType
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class LikeRepositoryImpl(
    db: CoroutineDatabase
) : LikeRepository {

    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()

    override suspend fun likePost(userId: String, parentId: String,parentType: Int): Boolean {
        val isUserExist = users.findOneById(userId) != null
        return if (isUserExist) {
            likes.insertOne(Like(userId, parentId,parentType))
            true
        } else {
            false
        }
    }

    override suspend fun unlikePost(userId: String, parentId: String): Boolean {
        val isUserExist = users.findOneById(userId) != null
        return if (isUserExist) {
            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            true
        } else {
            false
        }
    }

    override suspend fun removeLikeForParent(parentId: String) {
        likes.deleteMany(Like::parentId eq parentId)
    }
}