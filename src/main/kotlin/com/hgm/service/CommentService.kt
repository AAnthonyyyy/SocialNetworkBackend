package com.hgm.service

import com.hgm.data.models.Comment
import com.hgm.data.repository.comment.CommentRepository
import com.hgm.data.requests.AddCommentRequest
import com.hgm.utils.Constants

class CommentService(
    private val repository: CommentRepository
) {

    suspend fun addComment(request: AddCommentRequest,userId: String): ValidationEvent {
        request.apply {
            if (postId.isBlank() || comment.isBlank()) {
                return ValidationEvent.FieldEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvent.CommentTooLong
            }
        }
        repository.addComment(
            Comment(
                userId = userId,
                postId = request.postId,
                comment = request.comment,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentByPost(postId: String): List<Comment> {
        return repository.getCommentByPost(postId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return repository.getComment(commentId)
    }


    sealed class ValidationEvent {
        object FieldEmpty : ValidationEvent()
        object CommentTooLong : ValidationEvent()
        object Success : ValidationEvent()
    }
}