package com.hgm.service.chat

import com.google.gson.Gson
import com.hgm.data.repository.chat.ChatRepository
import com.hgm.data.webcosket.WsClientMessage
import com.hgm.data.webcosket.WsServerMessage
import com.hgm.utils.WebSocketObject
import io.ktor.http.cio.websocket.*
import org.litote.kmongo.text
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {
    //使用并发哈希图，保存当聊天室在线的成员
    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(
        userId: String,
        socket: WebSocketSession
    ) {
        onlineUsers[userId] = socket
    }

    fun disConnect(userId: String) {
        if (onlineUsers.containsKey(userId)) {
            onlineUsers.remove(userId)
        }
    }

    suspend fun sendMessage(ownUSerId: String, gson: Gson, message: WsClientMessage) {
        //先把消息插入数据库，但是chatId暂时为空，在如果第一次聊天生成chatId的时候，更新messages中的chatId
        val newMessage = message.toMessage(ownUSerId)
        //从在线用户中找到发信息的用户
        val wsServerMessage = WsServerMessage(
            sendId = ownUSerId,
            receiveId = message.receiveId,
            text = message.text,
            timestamp = System.currentTimeMillis(),
            chatId = message.chatId
        )
        val frameText = gson.toJson(wsServerMessage)
        onlineUsers[ownUSerId]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))
        onlineUsers[message.receiveId]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))

        if (!repository.doesChatByUsersExist(ownUSerId, message.receiveId)) {
            //判断当前两人是否已经存在对话，没有则新建一个（相当于第一次聊天）
            val chatId = repository.insertChat(
                userId1 = ownUSerId,
                userId2 = message.receiveId,
                messageId = newMessage.id
            )
            println("chatId2: $chatId")
            repository.insertMessage(newMessage.copy(chatId = chatId))
        } else {
            //如果有的话，就接着上次的对话
            repository.insertMessage(newMessage)
            message.chatId?.let { chatId ->
                repository.updateLastMessageForChat(
                    chatId = chatId,
                    lastMessageId = newMessage.id
                )
            }
        }
    }
}