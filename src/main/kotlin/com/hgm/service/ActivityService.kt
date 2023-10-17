package com.hgm.service

import com.hgm.data.models.Activity
import com.hgm.data.repository.activity.ActivityRepository
import com.hgm.data.repository.comment.CommentRepository
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.utils.ActivityType
import com.hgm.data.utils.ParentType
import com.hgm.utils.Constants

class ActivityService(
    private val repository: ActivityRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {

    suspend fun createLikeActivity(activity: Activity) {
        repository.createActivity(activity)
    }

    suspend fun createCommentActivity(
        byUserId: String,
        postId: String,
    ): Boolean {
        val toUserId = postRepository.getPost(postId)?.userId ?: return false

        //忽略本人点赞自己的内容时会创建活动
        if (byUserId==toUserId){
            return false
        }

        repository.createActivity(
            Activity(
                parentId = postId,
                byUserId = byUserId,
                toUserId = toUserId,
                timestamp = System.currentTimeMillis(),
                type = ActivityType.CommentedOnPost.type,
            )
        )
        return true
    }

    /**
     * xxx 点赞了 xxx的帖子
     * xxx 点赞了 xxx的评论
     * xxx 评论了 xxx的帖子
     * xxx 关注了 xxx
     */

    suspend fun createLikeActivity(
        byUserId: String,
        parentType: ParentType,//这里的父类型细分为两种：点赞帖子、点赞评论
        parentId: String
    ): Boolean {
        val toUserId = when (parentType) {
            is ParentType.Post -> {
                postRepository.getPost(parentId)?.userId
            }

            is ParentType.Comment -> {
                commentRepository.getComment(parentId)?.userId
            }

            is ParentType.None -> return false
        } ?: return false

        if (byUserId==toUserId){
            return false
        }

        repository.createActivity(
            Activity(
                parentId = parentId,
                byUserId = byUserId,
                toUserId = toUserId,
                timestamp = System.currentTimeMillis(),
                type = when (parentType) {
                    ParentType.Post -> ActivityType.LikedPost.type
                    ParentType.Comment -> ActivityType.LikedComment.type
                    ParentType.None -> ActivityType.LikedPost.type
                },
            )
        )
        return true
    }

    suspend fun deleteActivity(activityId: String): Boolean {
        return repository.deleteActivity(activityId)
    }

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = Constants.DEFAULT_ACTIVITY_PAGE,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Activity> {
        return repository.getActivitiesForUser(userId, page, pageSize)
    }
}