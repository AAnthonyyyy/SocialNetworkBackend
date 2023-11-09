package com.hgm.utils

object Constants {
    const val DATABASE_NAME = "social_network_backend"

    const val DEFAULT_POST_PAGE = 0
    const val DEFAULT_POST_PAGE_SIZE = 15
    const val DEFAULT_ACTIVITY_PAGE = 0
    const val DEFAULT_ACTIVITY_PAGE_SIZE = 10
    const val DEFAULT_LIKE_PAGE = 0
    const val DEFAULT_LIKE_PAGE_SIZE = 10

    const val KEY_CLAIM_USER_ID = "userId"

    const val MAX_COMMENT_LENGTH = 1000
    const val BASE_URL = "http://192.168.31.163:8080/"
    //const val BASE_URL = "http://172.20.10.4:8080/"
    const val PROFILE_PICTURE_PATH = "build/resources/main/static/profile_pictures/"
    const val BANNER_PICTURE_PATH = "build/resources/main/static/banner_pictures/"
    const val POST_PICTURE_PATH = "build/resources/main/static/post_pictures/"
    const val DEFAULT_PROFILE_PICTURE_PATH = "${BASE_URL}profile_pictures/default_avatar.svg"
    const val DEFAULT_BANNER_PICTURE_PATH = "${BASE_URL}profile_pictures/default_banner.png"
}