package com.hgm.routes

import com.google.gson.Gson
import com.hgm.data.webcosket.WsClientMessage
import com.hgm.data.webcosket.WsServerMessage
import com.hgm.service.chat.ChatController
import com.hgm.service.chat.ChatService
import com.hgm.service.chat.ChatSession
import com.hgm.utils.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.java.KoinJavaComponent.inject


fun Route.getChatsForUser(service: ChatService) {
    authenticate {
        get("/api/chat/chats") {
            val chats = service.getChatsForUser(ownUserId = call.userId)
            call.respond(
                HttpStatusCode.OK,
                chats
            )
        }
    }
}


fun Route.getMessagesForChat(service: ChatService) {
    authenticate {
        get("/api/chat/messages") {
            val chatId = call.parameters[QueryParams.PARAM_CHAT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            //检查chatId是否属于该用户
            val doesChatBelongUser = service.doesChatBelongUser(chatId = chatId, ownUserId = call.userId)
            if (!doesChatBelongUser) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }

            val messages = service.getMessagesForChat(
                chatId = chatId,
                page = page,
                pageSize = pageSize
            )
            call.respond(
                HttpStatusCode.OK,
                messages
            )
        }
    }
}


fun Route.chatWebSocket(chatController: ChatController) {
    authenticate {
        webSocket("/api/chat/websocket") {
            //接收会话
            //val session = call.sessions.get<ChatSession>() ?: kotlin.run {
            //    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "没有会话"))
            //    return@webSocket
            //}
            chatController.onJoin(call.userId, this)
            println("WebSocket 连接成功")

            try {
                //接收客户端传入的消息
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            // json格式：0#{"name" : "hgm"}
                            val frameText = frame.readText()
                            println("接收到的消息：$frameText")
                            val tagIndex = frameText.indexOf("#")
                            if (tagIndex == -1) {
                                return@consumeEach
                            }

                            //截取类型和内容
                            val type = frameText.substring(0, tagIndex).toIntOrNull() ?: return@consumeEach
                            val json = frameText.substring(tagIndex + 1, frameText.length)
                            handleWebSocket(call.userId, chatController, frameText, type, json)
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                //关闭websocket
                chatController.disConnect(call.userId)
            }
        }
    }
}


suspend fun handleWebSocket(
    ownUserId:String,
    chatController: ChatController,
    frameText: String,
    type: Int,
    json: String
) {
    val gson by inject<Gson>(Gson::class.java)
    when (type) {
        WebSocketObject.MESSAGE.ordinal -> {
            //把传入的json转成客户端信息对象
            val message = gson.fromJsonOrNull(json, WsClientMessage::class.java) ?: return
            chatController.sendMessage(ownUserId,gson, message)
        }
        else -> Unit
    }
}