package com.hgm.data.repository.comment

import com.hgm.data.models.Comment
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CommentRepositoryImpl(
    db: CoroutineDatabase
) : CommentRepository {

    private val comments = db.getCollection<Comment>()

    override suspend fun addComment(comment: Comment) {
        comments.insertOne(comment)
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        return comments.deleteOneById(commentId).deletedCount > 0
    }

    override suspend fun getCommentByPost(postId: String): List<Comment> {
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(commentId)
    }
}