package com.example.myapplication.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Entry Screen of the app with the navigator
 */
@Composable
fun Dashboard() {
    val navController = rememberNavController()

    //Navigator to navigate between the screen start with dashboard
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                //buttons to switch between the screens
                Button(onClick = { navController.navigate("open_screen") }) {
                    Text("Open ToDo's", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("close_screen") }) {
                    Text("Close ToDo's", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        //screen for the open tasks
        composable("open_screen") {
            val context = LocalContext.current
            OpenScreen(
                context = context,
                navController = navController
            )
        }
        //screen for the closed tasks
        composable("close_screen") {
            val context = LocalContext.current
            CloseScreen(
                context = context,
                navController = navController
            )
        }
    }
}