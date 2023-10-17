package com.hgm.data.utils

sealed class ActivityType(val type: Int) {
    object LikedPost : ActivityType(0)
    object LikedComment : ActivityType(1)
    object CommentedOnPost : ActivityType(2)
    object FollowedYou : ActivityType(3)
}
