package com.hgm.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Activity(
    val parentId: String,
    val byUserId: String,
    val toUserId: String,
    val timestamp: Long,
    val type: Int,
    @BsonId
    val id: String = ObjectId().toString(),
)
