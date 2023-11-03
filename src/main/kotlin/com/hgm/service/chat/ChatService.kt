package com.hgm.service.chat

import com.hgm.data.model.Chat
import com.hgm.data.model.Message
import com.hgm.data.repository.chat.ChatRepository

class ChatService(
    private val repository: ChatRepository
) {
    suspend fun doesChatBelongUser(chatId: String, ownUserId: String): Boolean  {
        return repository.doesChatBelongUser(chatId, ownUserId)
    }

    suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): List<Message> {
        return repository.getMessagesForChat(chatId, page, pageSize)
    }

    suspend fun getChatsForUser(ownUserId: String): List<Chat> {
        return repository.getChatsForUser(ownUserId)
    }
}