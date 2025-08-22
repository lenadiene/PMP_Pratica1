package com.example.weatherapp.db.fb

import com.example.weatherapp.model.City
import com.google.android.gms.maps.model.LatLng

class FBCity {
    var name: String? = null
    var lat: Double? = null
    var lng: Double? = null
    var monitored: Boolean = false   // Novo campo

    fun toCity(): City {
        val latlng = if (lat != null && lng != null) LatLng(lat!!, lng!!) else null
        return City(
            name = name!!,
            location = latlng,
            weather = null,
            isMonitored = monitored,
            salt = System.currentTimeMillis()
        )
    }
}

fun City.toFBCity(): FBCity {
    val fbCity = FBCity()
    fbCity.name = this.name
    fbCity.lat = this.location?.latitude
    fbCity.lng = this.location?.longitude
    fbCity.monitored = this.isMonitored
    return fbCity
}
