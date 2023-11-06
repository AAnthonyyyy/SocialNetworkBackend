package com.hgm.data.responses

data class ChatDto(
    val chatId:String,
    val remoteUserId:String?,
    val remoteUsername: String?,
    val remoteProfilePictureUrl: String?,
    val lastMessage: String?,
    val timestamp: Long?
)