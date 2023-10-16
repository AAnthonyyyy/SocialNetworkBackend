package com.hgm.data.repository.comment

import com.hgm.data.models.Comment

interface CommentRepository {

    suspend fun addComment(comment: Comment)
    suspend fun deleteComment(commentId: String): Boolean
    suspend fun getCommentByPost(postId: String): List<Comment>
    suspend fun getComment(commentId: String): Comment?
}