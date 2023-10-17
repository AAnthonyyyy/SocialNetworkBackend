package com.hgm.data.responses

data class ProfileResponse(
    val username: String,
    val profilePictureUrl: String,
    val bio: String,//个人履历
    val topSkillUrls: List<String>,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,
    val followingCount: Int,
    val followedCount: Int,
    val postCount: Int,
    val isOwnProfile: Boolean,
    val isFollowing: Boolean
)
