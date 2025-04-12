package com.example.lyceumapp.ui.theme.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.HtmlText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.example.lyceumapp.R
import com.example.lyceumapp.ui.theme.LyceumAppTheme
import com.example.lyceumapp.ui.theme.network.ApiClient
import com.example.lyceumapp.ui.theme.sampledata.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.format.TextStyle
import com.example.lyceumapp.ui.theme.network.fetchNews

@Composable
fun MainScreen(navController: NavController) {

    var request by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp, horizontal = 30.dp)
    ) {

        Text(
            "НОВОСТИ",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Button(
                modifier = Modifier.size(29.dp),
                onClick = { navController.navigate("notifications") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Unspecified
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Перейти к уведомлениям",
                    modifier = Modifier
                        .size(29.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            }

            Button(
                modifier = Modifier.size(29.dp),
                onClick = { navController.navigate("profile") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Unspecified
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Перейти к профилю",
                    modifier = Modifier
                        .size(29.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    var newsList by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null

        try {

            val newsIds = (6676..6695).reversed()

            val deferredNews = newsIds.map { id ->
                async {
                    fetchNews(id)
                }
            }

            newsList = deferredNews.awaitAll()
                .filterNotNull()
                .sortedByDescending { it.id }

            isLoading = false

        } catch (e: Exception) {
            error = "Ошибка: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 100.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = request,
            onValueChange = { request = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Поиск...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Поиск",
                    modifier = Modifier
                        .size(29.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                itemsIndexed(newsList) { index, newsItem ->
                    NewsCard(news = newsItem) {
                        navController.navigate("news_detail/${newsItem.id}")
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun MainScreenPreview() {
    LyceumAppTheme {
        val context = LocalContext.current
        val navController = TestNavHostController(context)
        MainScreen(navController = navController)
    }
}