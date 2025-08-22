package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import com.example.monitor.ForecastMonitor
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
                     private val service : WeatherService,
                     private val monitor: ForecastMonitor,
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
    fun update(city: City) {
        db.update(city.toFBCity())
    }
    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun add(name: String, location: LatLng? = null) {
        db.add(City(name = name, location = location).toFBCity())
    }
    fun loadBitmap(name: String) {
        val city = _cities[name]
        service.getBitmap(city?.weather!!.imgUrl) { bitmap ->
            val newCity = city.copy(
                weather = city.weather?.copy(
                    bitmap = bitmap
                )
            )
            _cities.remove(name)
            _cities[name] = newCity
        }
    }
    fun loadUser() {
        db.loadUser()
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
        monitor.cancelAll()
    }

    override fun onCityAdded(city: FBCity) {
         _cities[city.name!!] = city.toCity()
        monitor.updateCity(city.toCity())
    }
    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) { _city.value = tmp?.copy() }


    override fun onCityUpdated(city: FBCity) {
        val oldCity = _cities[city.name]
        val newCity = city.toCity().copy(
            weather = oldCity?.weather,
            forecast = oldCity?.forecast
        )
        _cities[city.name!!] = newCity

        if (_city.value?.name == city.name) {
            _city.value = newCity
        }

        monitor.updateCity(newCity) // <-- agenda/cancela worker da cidade
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
        if (_city.value?.name == city.name) {
            _city.value = null
        }
        monitor.cancelCity(city.toCity()) // <-- cancela worker dessa cidade
    }
}



class MainViewModelFactory(
    private val db: FBDatabase,
    private val service: WeatherService,
    private val monitor: ForecastMonitor   // <-- novo parâmetro
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service, monitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

