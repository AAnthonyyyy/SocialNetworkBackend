package com.hgm.data.responses


data class ProfileResponse(
    val userId: String,
    val username: String,
    val profilePictureUrl: String,
    val bannerUrl:String?,
    val bio: String,//个人履历
    val topSkillUrls: List<SkillDto>,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,
    val followingCount: Int,
    val followedCount: Int,
    val postCount: Int,
    val isOwnProfile: Boolean,
    val isFollowing: Boolean
)
