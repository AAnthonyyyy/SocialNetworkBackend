package com.hgm.data.repository.comment

import com.hgm.data.model.Comment
import com.hgm.data.responses.CommentResponse

interface CommentRepository {

    suspend fun addComment(comment: Comment):String
    suspend fun deleteComment(commentId: String): Boolean
    suspend fun deleteCommentsForPost(postId: String): Boolean
    suspend fun getCommentForPost(postId: String,ownUserId:String): List<CommentResponse>
    suspend fun getComment(commentId: String): Comment?
}