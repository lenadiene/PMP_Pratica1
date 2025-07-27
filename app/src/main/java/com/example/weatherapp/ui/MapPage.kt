package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.example.weatherapp.R
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun MapPage(viewModel: MainViewModel) {

    val context = LocalContext.current
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }



    val camPosState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camPosState,
        onMapClick = { latLng ->
            viewModel.add(latLng) // ✅ Passo 5 — localização → nome automático
        },
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true
        )
    ) {
        // Marcadores fixos


        // Marcadores dinâmicos das cidades favoritas
        viewModel.cities.forEach {
            val location = it.location
            if (location != null) {
                LaunchedEffect(it.name) {
                    if (it.weather == null) {
                        viewModel.loadWeather(it.name)
                    }
                }

                LaunchedEffect(it.weather) {
                    if (it.weather != null && it.weather!!.bitmap == null) {
                        viewModel.loadBitmap(it.name)
                    }
                }

                val image = it.weather?.bitmap
                    ?: getDrawable(context, R.drawable.loading)!!.toBitmap()

                val marker = BitmapDescriptorFactory.fromBitmap(image.scale(120, 120))

                Marker(
                    state = MarkerState(position = location),
                    title = it.name,
                    icon = marker,
                    snippet = it.weather?.desc ?: "Carregando..."
                )
            }
        }

    }
}

