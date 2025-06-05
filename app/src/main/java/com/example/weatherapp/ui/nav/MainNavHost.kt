package com.example.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.ui.HomePage
import com.example.weatherapp.ui.ListPage
import com.example.weatherapp.ui.MapPage
import com.example.weatherapp.viewmodel.MainViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel // ✅ ViewModel recebido aqui
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.toString()
    ) {
        composable(Route.Home.toString()) {
            HomePage(viewModel = viewModel) // ✅ passando para a Home
        }
        composable(Route.List.toString()) {
            ListPage(viewModel = viewModel) // ✅ passando para a List
        }
        composable(Route.Map.toString()) {
            MapPage(viewModel = viewModel) // ✅ passando para o Mapa
        }
    }
}
