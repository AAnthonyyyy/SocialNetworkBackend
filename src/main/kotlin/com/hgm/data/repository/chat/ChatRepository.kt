package com.hgm.data.repository.chat

import com.hgm.data.model.Chat
import com.hgm.data.model.Message

interface ChatRepository {

    suspend fun doesChatBelongUser(chatId: String, ownUserId: String): Boolean

    suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): List<Message>

    suspend fun getChatsForUser(ownUserId: String): List<Chat>

    suspend fun insertMessage(message: Message)

    suspend fun insertChat(userId1:String, userId2:String,messageId:String)

    suspend fun doesChatByUsersExist(userId1: String, userId2: String): Boolean

    suspend fun updateLastMessageForChat(chatId:String, lastMessageId: String)
}