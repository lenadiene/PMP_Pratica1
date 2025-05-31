package com.example.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.ui.HomePage
import com.example.weatherapp.ui.ListPage
import com.example.weatherapp.ui.MapPage

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.toString() // Converta para string
    ) {
        composable(Route.Home.toString()) { HomePage() }
        composable(Route.List.toString()) { ListPage() }
        composable(Route.Map.toString()) { MapPage() }
    }
}