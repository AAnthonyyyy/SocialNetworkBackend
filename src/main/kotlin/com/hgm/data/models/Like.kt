package com.hgm.data.models

import com.hgm.data.utils.ParentType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Like(
    val userId: String,
    val parentId: String,
    val parentType: Int,
    @BsonId
    val id: String = ObjectId().toString(),
)
