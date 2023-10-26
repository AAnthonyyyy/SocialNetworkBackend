package com.hgm.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * followers:追随者（我的粉丝）
 * following：拥护者（我的关注）
 */
data class Following(
    val followingUserId:String,
    val followedUserId:String,
    @BsonId
    val id:String=ObjectId().toString(),
)
