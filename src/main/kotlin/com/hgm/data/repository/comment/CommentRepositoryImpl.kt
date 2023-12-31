package com.hgm.data.repository.comment

import com.hgm.data.model.Comment
import com.hgm.data.model.Like
import com.hgm.data.model.Post
import com.hgm.data.responses.CommentResponse
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class CommentRepositoryImpl(
    db: CoroutineDatabase
) : CommentRepository {

    private val posts = db.getCollection<Post>()
    private val likes = db.getCollection<Like>()
    private val comments = db.getCollection<Comment>()


    override suspend fun addComment(comment: Comment): String {
        comments.insertOne(comment)
        val oldCommentCount = posts.findOneById(comment.postId)?.likeCount ?: 0
        posts.updateOneById(comment.postId, setValue(Post::commentCount, oldCommentCount + 1))
        return comment.id
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        return comments.deleteOneById(commentId).deletedCount > 0
    }

    override suspend fun deleteCommentsForPost(postId: String): Boolean {
        return comments.deleteMany(
            Comment::postId eq postId
        ).wasAcknowledged()//返回删除操作是否成功
    }

    override suspend fun getCommentForPost(postId: String, ownUserId: String): List<CommentResponse> {
        return comments.find(Comment::postId eq postId).toList().map { comment ->
            //查询我是否点赞了评论
            val isLiked = likes.findOne(
                and(
                    Like::userId eq ownUserId,
                    Like::parentId eq comment.id
                )
            ) != null
            CommentResponse(
                id = comment.id,
                username = comment.username,
                profilePictureUrl = comment.profilePictureUrl,
                timestamp = comment.timestamp,
                comment = comment.comment,
                likeCount = comment.likeCount,
                isLiked = isLiked
            )
        }

    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(commentId)
    }
}