package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import com.example.weatherapp.api.WeatherService
import com.example.weatherapp.api.toForecast
import com.example.weatherapp.api.toWeather
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.db.fb.FBCity
import com.example.weatherapp.db.fb.FBUser
import com.example.weatherapp.db.fb.toFBCity
import com.example.weatherapp.db.fb.toUser
import com.example.weatherapp.model.City
import com.example.weatherapp.model.User
import com.google.android.gms.maps.model.LatLng
import com.example.weatherapp.ui.nav.Route


class MainViewModel (private val db: FBDatabase,
                     private val service : WeatherService
): ViewModel(), FBDatabase.Listener {

    private val _cities = mutableStateMapOf<String, City>()
    val cities : List<City>
        get() = _cities.values.toList()
    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun add(name: String, location: LatLng? = null) {
        db.add(City(name = name, location = location).toFBCity())
    }

    fun add(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name=name, location=LatLng(lat, lng)).toFBCity())
            }
        }
    }
    fun add(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location).toFBCity())
            }
        }
    }
    fun loadWeather(name: String) {
        service.getWeather(name) { apiWeather ->
            val newCity = _cities[name]!!.copy( weather = apiWeather?.toWeather() )
            _cities.remove(name)
            _cities[name] = newCity
        }
    }
    fun loadForecast(name: String) {
        service.getForecast(name) { apiForecast ->
            val newCity = _cities[name]!!.copy( forecast = apiForecast?.toForecast() )
            _cities.remove(name)
            _cities[name] = newCity
            city = if (city?.name == name) newCity else city
        }
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _cities.clear()
    }

    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }
    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) { _city.value = tmp?.copy() }

    override fun onCityUpdated(city: FBCity) {
        _cities.remove(city.name)
        _cities[city.name!!] = city.toCity()
        if (_city.value?.name == city.name) { _city.value = city.toCity() }
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
        if (_city.value?.name == city.name) { _city.value = null }
    }
}


class MainViewModelFactory(private val db: FBDatabase,private val service : WeatherService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
