package com.hgm.plugins

import com.hgm.service.chat.ChatSession
import com.hgm.utils.userId
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*

//fun Application.configureSessions() {
//    install(Sessions) {
//        cookie<ChatSession>("SESSION")
//    }
//
//    //设置拦截器，拦截每一个访问服务器的请求
//    intercept(ApplicationCallPipeline.Features) {
//        if (call.sessions.get<ChatSession>() == null) {
//            //如果没有会话则自动生成一个
//            val userId = call.parameters["userId"] ?: return@intercept
//            call.sessions.set(ChatSession(userId, generateNonce()))
//        }
//    }
//}