package com.hgm.data.requests

import com.hgm.data.responses.SkillDto

data class UpdateProfileRequest(
    val username: String,
    val bio: String,
    val githubUrl: String,
    val instagramUrl: String,
    val linkedInUrl: String,
    val skills: List<SkillDto>,
    val profileImageChange: Boolean = false
)
