package com.example.music

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.music.screen.Home
import com.example.music.screen.LecteurScreen

@Composable
fun Route(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        builder = {
            composable("home") {
                Home(navController)
            }
            composable("LecteurAudio"){
                LecteurScreen ()
            }
        }
    )
}