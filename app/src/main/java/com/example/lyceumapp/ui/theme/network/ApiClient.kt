package com.example.lyceumapp.ui.theme.network

import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URI

object ApiClient {
    private const val LOGIN_URL = "https://www.1511.ru/accounts/login"

    private val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }))
        .build()

    fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val mainHandler = Handler(Looper.getMainLooper())

        val getRequest = Request.Builder()
            .url(LOGIN_URL)
            .headers(
                mapOf(
                    "User-Agent" to "Mozilla/5.0",
                    "X-Requested-With" to "XMLHttpRequest",
                    "Referer" to "https://www.1511.ru/"
                ).toHeaders()
            )
            .build()

        client.newCall(getRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                mainHandler.post { onError("Ошибка сети: ${e.message}") }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val csrfToken = response.header("Set-Cookie")
                        ?.split(";")
                        ?.firstOrNull { it.trim().startsWith("lcsrf=") }
                        ?.substringAfter("=")
                        ?.substringBefore(';')
                        ?.trim()

                    if (csrfToken.isNullOrEmpty()) {
                        mainHandler.post { onError("CSRF token не найден") }
                        return
                    }

                    val formBody = FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .add("lcsrf", csrfToken)
                        .build()

                    val postRequest = Request.Builder()
                        .url(LOGIN_URL)
                        .post(formBody)
                        .headers(
                            mapOf(
                                "User-Agent" to "Mozilla/5.0",
                                "X-CSRFToken" to csrfToken,
                                "Content-Type" to "application/x-www-form-urlencoded; charset=UTF-8",
                                "Origin" to "https://www.1511.ru",
                                "Referer" to "https://www.1511.ru/",
                                "X-Requested-With" to "XMLHttpRequest"
                            ).toHeaders()
                        )
                        .build()

                    client.newCall(postRequest).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            mainHandler.post { onError("Ошибка сети: ${e.message}") }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            try {
                                val responseBody = response.body?.string()

                                if (responseBody.isNullOrEmpty()) {
                                    mainHandler.post { onError("Пустой ответ от сервера") }
                                    return
                                }

                                if (responseBody.contains("CSRF", true)) {
                                    mainHandler.post { onError("Ошибка CSRF проверки") }
                                    return
                                }

                                val json = JSONObject(responseBody)
                                val formMessage = json.optString("formMessage", "")


                                mainHandler.post {
                                    if (formMessage == "Вход выполнен...") {
                                        onSuccess()
                                    } else {
                                        onError(json.optString("errorMessage", "Ошибка авторизации"))
                                    }
                                }
                            } catch (e: Exception) {
                                mainHandler.post { onError("Ошибка обработки ответа: ${e.message}") }
                            }
                        }
                    })
                } catch (e: Exception) {
                    mainHandler.post { onError("Ошибка обработки CSRF: ${e.message}") }
                }
            }
        })
    }

    fun getCookies(): Map<String, String> {
        val url = "https://www.1511.ru".toHttpUrl()
        val cookies = client.cookieJar.loadForRequest(url)
        return cookies.associate { it.name to it.value }
    }
}