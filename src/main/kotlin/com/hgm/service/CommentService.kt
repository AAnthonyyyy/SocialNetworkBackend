package com.hgm.service

import com.hgm.data.model.Comment
import com.hgm.data.repository.comment.CommentRepository
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.requests.AddCommentRequest
import com.hgm.data.responses.CommentResponse
import com.hgm.utils.Constants

class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {

    suspend fun addComment(request: AddCommentRequest, userId: String): ValidationEvent {
        request.apply {
            if (postId.isBlank() || comment.isBlank()) {
                return ValidationEvent.FieldEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvent.CommentTooLong
            }
        }

        val user = userRepository.getUserById(userId) ?: return ValidationEvent.UserNotFound
        commentRepository.addComment(
            Comment(
                userId = userId,
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                likeCount = 0,
                postId = request.postId,
                comment = request.comment,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return commentRepository.deleteComment(commentId)
    }

    suspend fun getCommentForPost(postId: String,ownUserId:String): List<CommentResponse> {
        return commentRepository.getCommentForPost(postId,ownUserId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return commentRepository.getComment(commentId)
    }

    suspend fun deleteCommentForPost(postId: String) {
        commentRepository.deleteCommentsForPost(postId)
    }


    sealed class ValidationEvent {
        object FieldEmpty : ValidationEvent()
        object CommentTooLong : ValidationEvent()
        object UserNotFound : ValidationEvent()
        object Success : ValidationEvent()
    }
}