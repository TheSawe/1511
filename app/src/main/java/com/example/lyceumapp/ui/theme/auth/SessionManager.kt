package com.example.lyceumapp.ui.theme.auth
//
//import android.content.Context
//import okhttp3.Response
//import java.net.CookieManager
//
object SessionManager {
//    private const val PREF_NAME = "session_prefs"
//    private const val KEY_CSRF = "csrf_token"
//
//    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//    internal val cookieManager = CookieManager()
//
//    init {
//        // Восстанавливаем куки при старте приложения
//        val cookies = prefs.getStringSet("cookies", emptySet()) ?: emptySet()
//        cookies.forEach { cookie ->
//            cookieManager.cookieStore.add(null, HttpCookie.parse(cookie).first())
//        }
//    }
//
//    fun saveSession(response: Response) {
//        // Сохраняем куки
//        val cookies = response.headers("Set-Cookie")
//        val cookieSet = cookies.toSet()
//        prefs.edit {
//            putStringSet("cookies", cookieSet)
//        }
//
//        // Сохраняем CSRF-токен
//        val csrfToken = response.header("Set-Cookie")
//            ?.split(";")
//            ?.firstOrNull { it.startsWith("lcsrf=") }
//            ?.substringAfter("=")
//        csrfToken?.let {
//            prefs.edit {
//                putString(KEY_CSRF, it)
//            }
//        }
//    }
//
//    fun getCachedCsrfToken(): String? {
//        return prefs.getString(KEY_CSRF, null)
//    }
//
//    fun clearSession() {
//        prefs.edit { clear() }
//        cookieManager.cookieStore.removeAll()
//    }
}