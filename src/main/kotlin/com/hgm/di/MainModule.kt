package com.hgm.di

import com.hgm.controller.user.UserController
import com.hgm.controller.user.UserControllerImpl
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

    single<UserController> {
        UserControllerImpl(get())
    }
}