package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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

    val recife = LatLng(-8.05, -34.9)
    val caruaru = LatLng(-8.27, -35.98)
    val joaopessoa = LatLng(-7.12, -34.84)

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
        Marker(
            state = MarkerState(position = recife),
            title = "Recife",
            snippet = "Marcador em Recife",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
        Marker(
            state = MarkerState(position = caruaru),
            title = "Caruaru",
            snippet = "Marcador em Caruaru",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )
        Marker(
            state = MarkerState(position = joaopessoa),
            title = "João Pessoa",
            snippet = "Marcador em João Pessoa",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        )

        // Marcadores dinâmicos das cidades favoritas
        viewModel.cities.forEach {
            it.location?.let { pos ->
                Marker(
                    state = MarkerState(position = pos),
                    title = it.name,
                    snippet = "$pos"
                )
            }
        }
    }
}
