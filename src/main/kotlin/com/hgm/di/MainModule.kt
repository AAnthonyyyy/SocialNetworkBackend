package com.hgm.di

import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.repository.follow.FollowRepositoryImpl
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.repository.post.PostRepositoryImpl
import com.hgm.data.repository.user.UserRepository
import com.hgm.data.repository.user.UserRepositoryImpl
import com.hgm.util.Constants.DATABASE_NAME
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase(DATABASE_NAME)
    }

    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }

    single<PostRepository> {
        PostRepositoryImpl(get())
    }
}