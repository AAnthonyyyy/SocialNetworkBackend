package com.hgm.data.repository.follow

import com.hgm.data.model.Following
import com.hgm.data.model.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.inc

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
        println("关注者：$doesFollowingUserExist")
        println("被关注者：$doesFollowedUserExist")
        if (!doesFollowingUserExist || !doesFollowedUserExist) {
            return false
        }
        //关注者的关注量+1
        users.updateOneById(
            followingUserId,
            inc(User::followingCount,1)
        )
        //被关注者的粉丝量+1
        users.updateOneById(
            followedUserId,
            inc(User::followedCount,1)
        )
        follows.insertOne(
            Following(followingUserId, followedUserId)
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
        if (deleteResult.deletedCount>0){
            //关注者的关注量+1
            users.updateOneById(
                followingUserId,
                inc(User::followingCount,-1)
            )
            //被关注者的粉丝量+1
            users.updateOneById(
                followedUserId,
                inc(User::followedCount,-1)
            )
        }
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