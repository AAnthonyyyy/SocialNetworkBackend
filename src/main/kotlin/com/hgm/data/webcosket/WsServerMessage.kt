package com.hgm.data.webcosket

import com.hgm.data.model.Message

data class WsServerMessage(
    val sendId: String,
    val receiveId: String,
    val text: String,
    val timestamp: Long,
    val chatId: String?
) {
    fun toMessage(): Message {
        return Message(
            sendId = sendId,
            receiveId = receiveId,
            text = text,
            timestamp = timestamp,
            chatId = chatId
        )
    }
}

