package com.lucaslei.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucaslei.app.ui.todo.TodoScreen

sealed class Screen(val route: String) {
    object Todo : Screen("todo")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Todo.route
    ) {
        composable(Screen.Todo.route) {
            TodoScreen()
        }
    }
}
