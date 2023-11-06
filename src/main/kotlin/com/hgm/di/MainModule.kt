package com.hgm.di

import com.google.gson.Gson
import com.hgm.data.model.Message
import com.hgm.data.repository.activity.ActivityRepository
import com.hgm.data.repository.activity.ActivityRepositoryImpl
import com.hgm.data.repository.chat.ChatRepository
import com.hgm.data.repository.chat.ChatRepositoryImpl
import com.hgm.data.repository.comment.CommentRepository
import com.hgm.data.repository.comment.CommentRepositoryImpl
import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.repository.follow.FollowRepositoryImpl
import com.hgm.data.repository.like.LikeRepository
import com.hgm.data.repository.like.LikeRepositoryImpl
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.repository.post.PostRepositoryImpl
import com.hgm.data.repository.skill.SkillRepository
import com.hgm.data.repository.skill.SkillRepositoryImpl
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.repository.user.UserRepositoryImpl
import com.hgm.service.*
import com.hgm.service.chat.ChatController
import com.hgm.service.chat.ChatService
import com.hgm.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


val mainModule = module {
    // Database
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase(DATABASE_NAME)
    }

    // Repository
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
    single<PostRepository> {
        PostRepositoryImpl(get())
    }
    single<LikeRepository> {
        LikeRepositoryImpl(get())
    }
    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }
    single<ActivityRepository> {
        ActivityRepositoryImpl(get())
    }
    single<SkillRepository> {
        SkillRepositoryImpl(get())
    }
    single<ChatRepository> {
        ChatRepositoryImpl(get())
    }


    // Service
    single { UserService(get(), get()) }
    single { FollowService(get()) }
    single { PostService(get()) }
    single { LikeService(get(), get(), get()) }
    single { CommentService(get(), get()) }
    single { ActivityService(get(), get(), get()) }
    single { SkillService(get()) }
    single { ChatService(get()) }

    single { Gson() }

    single { ChatController(get()) }
}