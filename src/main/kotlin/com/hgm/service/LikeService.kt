package com.hgm.service

import com.hgm.data.models.Like
import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.repository.like.LikeRepository
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.responses.UserResponseItem

class LikeService(
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun likePost(userId: String, parentId: String, parentType: Int): Boolean {
        return likeRepository.likePost(userId, parentId, parentType)
    }

    suspend fun unlikePost(userId: String, parentId: String): Boolean {
        return likeRepository.unlikePost(userId, parentId)
    }

    suspend fun removeLikeForParent(parentId: String) {
        likeRepository.removeLikeForParent(parentId)
    }

    suspend fun getUsersWhoLikesParent(parentId: String, userId: String): List<UserResponseItem> {
        //获取喜欢该帖子或者评论的所有用户ID
        val usersId = likeRepository.getLikesForParent(parentId).map {
            it.userId
        }
        //查找这些用户的所有信息
        val users = userRepository.getUsers(usersId)
        //获取我所有关注的人
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
    }
}