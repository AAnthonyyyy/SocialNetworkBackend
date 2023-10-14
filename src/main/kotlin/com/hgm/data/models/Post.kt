package com.hgm.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(
    val userId: String,
    val imageUrl: String,
    val description: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString(),
)
