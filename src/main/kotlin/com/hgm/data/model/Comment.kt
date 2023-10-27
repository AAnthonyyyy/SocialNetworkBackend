package com.hgm.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    val userId: String,
    val username: String,
    val profilePictureUrl: String,
    val postId: String,
    val comment: String,
    val timestamp: Long,
    val likeCount: Int,
    @BsonId
    val id: String = ObjectId().toString()
)
