package com.hgm.data.repository.follow

import com.hgm.data.models.Following
import com.hgm.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class FollowRepositoryImpl(
    db: CoroutineDatabase
) : FollowRepository {
    private val follows = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun followUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean {
        // 添加关注之前要查询两人是否存在
        val doesFollowingUserExist = users.findOneById(followingUserId) != null
        val doesFollowedUserExist = users.findOneById(followedUserId) != null
        if (!doesFollowingUserExist || !doesFollowedUserExist) {
            return false
        }
        follows.insertOne(
            Following(
                followingUserId,
                followedUserId
            )
        )
        return true
    }


    override suspend fun unFollowUserIfExist(followingUserId: String, followedUserId: String): Boolean {
        // 虽然没有查询用户操作，但是在删除之前进行了条件过滤等同于查询用户是否存在
        val deleteResult = follows.deleteOne(
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        )
        return deleteResult.deletedCount > 0
    }

    override suspend fun checkUserFollowing(followingUserId: String, followedUserId: String): Boolean {
        return follows.findOne(
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        ) != null
    }

    override suspend fun getFollowsByUser(userId: String): List<Following> {
        return follows.find(Following::followingUserId eq userId).toList()
    }
}