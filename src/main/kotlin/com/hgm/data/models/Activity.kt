package com.hgm.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Activity(
    @BsonId
    val id: String = ObjectId().toString(),
    val parentId: String,
    val toUserId: String,
    val byUserId: String,
    val type: Int,
    val timestamp: Long
)
