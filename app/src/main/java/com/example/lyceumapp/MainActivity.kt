package com.example.lyceumapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lyceumapp.ui.theme.LyceumAppTheme
import com.example.lyceumapp.ui.theme.auth.LoginScreen
import com.example.lyceumapp.ui.theme.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LyceumAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("main") {
                            MainScreen(navController)
                        }
                        composable("news_detail/{newsId}") { backStackEntry ->
                            val newsId = backStackEntry.arguments?.getString("newsId")?.toIntOrNull()
//                            NewsDetailScreen(newsId = newsId)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LyceumAppTheme {
        LoginScreen(navController = rememberNavController())
    }
}