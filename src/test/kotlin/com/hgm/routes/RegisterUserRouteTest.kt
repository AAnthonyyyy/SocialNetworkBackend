package com.hgm.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.hgm.data.model.User
import com.hgm.data.requests.CreateAccountRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.di.testModule
import com.hgm.plugins.configureSerialization
import com.hgm.repository.user.FakeUserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Test


internal class RegisterUserRouteTest : KoinTest {

    private val userRepository by inject<FakeUserRepository>()
    private val gson: Gson = Gson()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(testModule)
        }
    }

    @BeforeTest
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun `测试一：发起注册请求，但是没有附加主体，返回BadRequest`() {
        withTestApplication(
            moduleFunction = {
                install(Routing) {
                    registerUser(userRepository)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/register"
            )

            //使用断言
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }


    @Test
    fun `测试二：发起注册请求，但是用户已经存在，响应不成功`() = runBlocking {
        // 先创建一个用户
        val user = User(
            email = "test@test.com",
            username = "test",
            password = "test",
            profileImageUrl = "",
            bio = "",
            githubUrl = "",
            instagramUrl = "",
            linkedInUrl = ""
        )
        userRepository.createUser(user)


        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    registerUser(userRepository)
                }
            }
        ) {
            // 模拟发起请求
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/register"
            ) {
                // 添加body并转成json上传
                addHeader("Content-Type","application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test",
                )
                setBody(gson.toJson(request))
            }


            // 接收响应
            val response = gson.fromJson(
                request.response.content ?:"",
                BaseResponse::class.java
            )
            // 断言是否失败
            assertThat(response.successful).isFalse()
        }
    }


    @Test
    fun `测试三：发起注册请求，但是body的内容为空，响应不成功`() {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    registerUser(userRepository)
                }
            }
        ) {
            // 模拟发起请求
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/register"
            ) {
                // 添加body并转成json上传
                addHeader("Content-Type","application/json")
                val request = CreateAccountRequest(
                    email = "",
                    username = "",
                    password = "",
                )
                setBody(gson.toJson(request))
            }


            // 接收响应
            val response = gson.fromJson(
                request.response.content ?:"",
                BaseResponse::class.java
            )
            // 断言是否失败
            assertThat(response.successful).isFalse()
        }
    }

    @Test
    fun `测试四：发起注册请求，验证正确，响应成功`() {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    registerUser(userRepository)
                }
            }
        ) {
            // 模拟发起请求
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/register"
            ) {
                // 添加body并转成json上传
                addHeader("Content-Type","application/json")
                val request = CreateAccountRequest(
                    email = "111",
                    username = "111",
                    password = "111",
                )
                setBody(gson.toJson(request))
            }


            // 接收响应
            val response = gson.fromJson(
                request.response.content ?:"",
                BaseResponse::class.java
            )
            // 断言是否失败
            assertThat(response.successful).isTrue()
        }
    }

}