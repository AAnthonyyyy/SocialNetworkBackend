package com.hgm.data.model

import com.hgm.data.responses.SkillDto
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val email: String,
    val username: String,
    val password: String,
    val profileImageUrl: String,
    val bannerUrl: String,
    val bio: String,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,
    val followingCount: Int = 0,
    val followedCount: Int = 0,
    val postCount: Int = 0,
    val skills: List<SkillDto> = listOf(),
    @BsonId
    val id: String = ObjectId().toString(),
)