package com.hgm.data.repository.comment

import com.hgm.data.models.Comment
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CommentRepositoryImpl(
    db: CoroutineDatabase
) : CommentRepository {

    private val comments = db.getCollection<Comment>()

    override suspend fun addComment(comment: Comment):String {
        comments.insertOne(comment)
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

    override suspend fun getCommentForPost(postId: String): List<Comment> {
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(commentId)
    }
}