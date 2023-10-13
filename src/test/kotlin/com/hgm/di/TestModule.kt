package com.hgm.di

import com.hgm.repository.user.FakeUserRepository
import org.koin.dsl.module

internal val testModule = module {
    single {
        FakeUserRepository()
    }
}