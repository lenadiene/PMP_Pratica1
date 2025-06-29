package com.example.weatherapp.db.fb

import com.example.weatherapp.model.City
import com.google.android.gms.maps.model.LatLng
class FBCity {
    var name: String? = null
    var lat: Double? = null
    var lng: Double? = null

    fun toCity(): City {
        val latlng = if (lat != null && lng != null) LatLng(lat!!, lng!!) else null
        return City(name!!, weather = null, location = latlng)
    }
}

fun City.toFBCity(): FBCity {
    val fbCity = FBCity()
    fbCity.name = this.name
    fbCity.lat = this.location?.latitude
    fbCity.lng = this.location?.longitude
    return fbCity
}