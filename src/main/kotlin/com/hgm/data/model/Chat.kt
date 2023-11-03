package com.hgm.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * 聊天对象（一对一、群组）
 */
data class Chat(
    val userIds: List<String>,
    val lastMessageId: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString()
)
