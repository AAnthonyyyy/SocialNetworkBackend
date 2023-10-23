package com.hgm.data.model

import com.hgm.data.responses.SkillDto
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Skill(
    @BsonId
    val id: String = ObjectId().toString(),
    val name: String,
    val imageUrl: String
) {
    fun toSkillResponse(): SkillDto {
        return SkillDto(
            name = name,
            imageUrl = imageUrl
        )
    }
}
