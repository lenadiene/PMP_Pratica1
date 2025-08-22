package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.monitor.ForecastMonitor
import com.example.weatherapp.api.WeatherService
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.ui.CityDialog
import com.example.weatherapp.ui.HomePage
import com.example.weatherapp.ui.nav.BottomNavBar
import com.example.weatherapp.ui.nav.BottomNavItem
import com.example.weatherapp.ui.nav.MainNavHost
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.ui.nav.Route
import com.example.weatherapp.viewmodel.MainViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.core.util.Consumer


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val fbDB = remember { FBDatabase() }
            val weatherService = remember { WeatherService() }
            val monitor = remember { ForecastMonitor(this) } // <-- cria ForecastMonitor

            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB, weatherService, monitor) // <-- passa monitor
            )

            LaunchedEffect(Unit) {
                viewModel.loadUser()
            }
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    val name = intent.getStringExtra("city")
                    val city = viewModel.cities.find { it.name == name }
                    viewModel.city = city
                    viewModel.page = Route.Home
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            val navController = rememberNavController()
            var showDialog by remember { mutableStateOf(false) }

            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.route == Route.List.toString()


            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )

            WeatherAppTheme {

                if (showDialog) {
                    CityDialog(
                        onDismiss = { showDialog = false },
                        onConfirm = { city ->
                            if (city.isNotBlank()) viewModel.add(city)
                            showDialog = false
                        }
                    )
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val user by remember { derivedStateOf { viewModel.user } }
                                Text("Bem-vindo/a! ${user?.name ?: "[não logado]"}")

                            },
                            actions = {
                                IconButton(onClick = {
                                    Firebase.auth.signOut()

                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair"
                                    )
                                }
                            }
                        )

                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton
                        )
                        BottomNavBar(viewModel, items)
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    LaunchedEffect(viewModel.page) {
                         navController.navigate(viewModel.page.toString()) {
                             // Volta pilha de navegação até HomePage (startDest).
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val fakeViewModel = remember { MainViewModel(
        db = TODO(),
        service = TODO(),
        monitor = TODO()
    ) }

    WeatherAppTheme {
        HomePage(viewModel = fakeViewModel)
    }
}