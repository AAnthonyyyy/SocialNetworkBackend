package com.hgm.data.repository.chat

import com.hgm.data.model.Chat
import com.hgm.data.model.Message
import com.hgm.data.model.User
import com.hgm.data.responses.ChatDto
import org.litote.kmongo.and
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class ChatRepositoryImpl(
    db: CoroutineDatabase
) : ChatRepository {

    private val chats = db.getCollection<Chat>()
    private val users = db.getCollection<User>()
    private val messages = db.getCollection<Message>()


    //获取该聊天对象的所有信息
    override suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): List<Message> {
        return messages.find(Message::chatId eq chatId)
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }

    override suspend fun getChatsForUser(ownUserId: String): List<ChatDto> {
        //查询自己id的所有对话列表
        return chats.find(Chat::userIds contains ownUserId)
            .descendingSort(Chat::timestamp)
            .toList()
            .map { chat ->
                val otherUserId = chat.userIds.find { it != ownUserId }
                val user = users.findOneById(otherUserId ?: "")
                val lastMessage = messages.findOneById(chat.lastMessageId)
                ChatDto(
                    chatId = chat.id,
                    remoteUserId = user?.id,
                    remoteUsername = user?.username,
                    remoteProfilePictureUrl = user?.profileImageUrl,
                    lastMessage = lastMessage?.text,
                    timestamp = chat.timestamp
                )
            }
    }

    override suspend fun doesChatBelongUser(chatId: String, ownUserId: String): Boolean {
        //查询该对话是否属于自己，只要两人ids中包含自己的id即可
        return chats.findOneById(chatId)?.userIds?.any { it == ownUserId } == true
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun insertChat(userId1: String, userId2: String, messageId: String): String {
        val chat = Chat(
            userIds = listOf(userId1, userId2),
            lastMessageId = messageId,
            timestamp = System.currentTimeMillis()
        )
        val chatId = chats.insertOne(chat).insertedId?.asObjectId().toString()
        println("chatId1: $chatId")
        messages.updateOneById(messageId, setValue(Message::chatId, chatId))
        return chat.id
    }

    override suspend fun doesChatByUsersExist(userId1: String, userId2: String): Boolean {
        //return chats.find(A
        //    "{${MongoOperator.and}: [ {\"users.id\":\"$userId1\"},{\"users.id\":\"$userId2\"} ]}"
        //).first() != null
        return chats.find(
            and(
                Chat::userIds contains userId1,
                Chat::userIds contains userId2,
            )
        ).first() != null
    }


    override suspend fun updateLastMessageForChat(chatId: String, lastMessageId: String) {
        chats.updateOneById(chatId, setValue(Chat::lastMessageId, lastMessageId))
    }
}