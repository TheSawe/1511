package com.example.lyceumapp.ui.theme.network

import android.util.Log
import com.example.lyceumapp.ui.theme.sampledata.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode


suspend fun fetchNews(id: Int): NewsItem? = withContext(Dispatchers.IO) {
    try {
        val cookies = ApiClient.getCookies()
        val document = Jsoup.connect("https://www.1511.ru/news/$id")
            .userAgent("Mozilla/5.0")
            .timeout(10000)
            .cookies(cookies)
            .get()

        val h1 = document.selectFirst("h1")
        val title = h1?.text() ?: "Без заголовка"

        val contentElement = h1?.nextElementSiblings()?.firstOrNull {
            it.text().trim().isNotEmpty()
        }

        val content = contentElement?.text()?.trim()
            ?: "Нет содержимого"

        val imageUrl = document.selectFirst("div.content")?.selectFirst("img")?.absUrl("src")
        
//        val author = document.selectFirst(".news-author")?.text() ?: "Автор не указан"
//        val posted = document.selectFirst(".news-author")?.selectFirst("br")?.text() ?: ""
//
//        val (date, time) = posted
//            .replace("Размещено: ", "")
//            .split(", ")
//
        val tags = document.selectFirst(".news-categories")?.text()
            ?.replace("Категории: ", "")
            ?.split(", ")
            ?.map { it.trim() }

        return@withContext NewsItem(id, title, content, imageUrl, tags?:emptyList())

    } catch (e: Exception) {
        Log.e("NewsLoader", "Ошибка загрузки $id", e)
        return@withContext null
    }
}