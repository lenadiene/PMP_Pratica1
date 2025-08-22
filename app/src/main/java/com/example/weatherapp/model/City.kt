package com.example.weatherapp.model

import com.google.android.gms.maps.model.LatLng

data class City(
    val name: String,
    var location: LatLng? = null,
    var weather: Weather? = null,
    var forecast: List<Forecast>? = null,
    var isMonitored: Boolean = false,   // Novo campo
    var salt: Long = System.currentTimeMillis() // "Força" atualização na UI
)
