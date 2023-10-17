package com.hgm.data.repository.comment

import com.hgm.data.models.Comment

interface CommentRepository {

    suspend fun addComment(comment: Comment):String
    suspend fun deleteComment(commentId: String): Boolean
    suspend fun deleteCommentsForPost(postId: String): Boolean
    suspend fun getCommentForPost(postId: String): List<Comment>
    suspend fun getComment(commentId: String): Comment?
}