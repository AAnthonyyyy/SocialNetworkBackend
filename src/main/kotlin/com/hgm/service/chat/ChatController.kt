package com.hgm.service.chat

import com.hgm.data.repository.chat.ChatRepository
import com.hgm.data.webcosket.WsClientMessage
import com.hgm.data.webcosket.WsServerMessage
import io.ktor.http.cio.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {
    //使用并发哈希图，保存当聊天室在线的成员
    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(
        chatSession: ChatSession,
        socket: WebSocketSession
    ) {
        onlineUsers[chatSession.userId] = socket
    }

    fun disConnect(userId: String) {
        if (onlineUsers.containsKey(userId)) {
            onlineUsers.remove(userId)
        }
    }

    suspend fun sendMessage(ownUSerId:String,frameText: String, message: WsClientMessage) {
        //从在线用户中找到发信息的用户
        onlineUsers[ownUSerId]?.send(Frame.Text(frameText))
        onlineUsers[message.receiveId]?.send(Frame.Text(frameText))

        //先把消息插入数据库，但是chatId暂时为空，在如果第一次聊天生成chatId的时候，更新messages中的chatId
        val newMessage = message.toMessage(ownUSerId)
        repository.insertMessage(newMessage)


        /*
            判断当前两人是否已经存在对话，没有则新建一个（相当于第一次聊天）
            如果有的话，就接着上次的对话
        */
        if (!repository.doesChatByUsersExist(ownUSerId, message.receiveId)) {
            repository.insertChat(
                userId1 = ownUSerId,
                userId2 = message.receiveId,
                messageId = newMessage.id
            )
        } else {
            message.chatId?.let { chatId ->
                repository.updateLastMessageForChat(
                    chatId = chatId,
                    lastMessageId = newMessage.id
                )
            }
        }
    }
}