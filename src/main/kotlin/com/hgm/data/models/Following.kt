package com.hgm.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Following(
    @BsonId
    val id:String=ObjectId().toString(),
    val followingUserId:String,// 我关注别人的ID
    val followedUserId:String// 别人关注我的ID
)
