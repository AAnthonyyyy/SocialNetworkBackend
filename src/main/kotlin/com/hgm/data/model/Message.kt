package com.hgm.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Message(
    val sendId: String,
    val receiveId: String,
    val text: String,
    val timestamp: Long,
    val chatId:String?,
    @BsonId
    val id: String = ObjectId().toString()
)
