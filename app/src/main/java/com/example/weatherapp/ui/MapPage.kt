package com.example.weatherapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun MapPage(viewModel: MainViewModel) {

    val recife = LatLng(-8.05, -34.9)
    val caruaru = LatLng(-8.27, -35.98)
    val joaopessoa = LatLng(-7.12, -34.84)

    val camPosState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camPosState,
        onMapClick = { latLng ->
            viewModel.add("Cidade@${latLng.latitude}:${latLng.longitude}", location = latLng)
        }
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

        // Marcadores dinâmicos
        viewModel.cities.forEach {
            if (it.location != null) {
                Marker(
                    state = MarkerState(position = it.location),
                    title = it.name,
                    snippet = "${it.location}"
                )
            }
        }
    }
}
