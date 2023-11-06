package com.hgm.data.webcosket

import com.hgm.data.model.Message

data class WsClientMessage(
    val receiveId: String,
    val text: String,
    val chatId: String?
) {
    fun toMessage(sendId: String): Message {
        return Message(
            sendId = sendId,
            receiveId = receiveId,
            text = text,
            timestamp = System.currentTimeMillis(),
            chatId = chatId
        )
    }
}

