package com.hgm.data.utils

sealed class ActivityType(val type: Int) {
    object LikedPost : ActivityType(0)
    object CommentedOnPost : ActivityType(1)
    object FollowedUser : ActivityType(2)
}
