package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.db.fb.FBCity
import com.example.weatherapp.db.fb.FBUser
import com.example.weatherapp.db.fb.toFBCity
import com.example.weatherapp.db.fb.toUser
import com.example.weatherapp.model.City
import com.example.weatherapp.model.User
import com.google.android.gms.maps.model.LatLng

class MainViewModel(private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {
    private val _cities = mutableStateListOf<City>()
    val cities: List<City>
        get() = _cities.toList()

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

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _cities.clear()
    }

    override fun onCityAdded(city: FBCity) {
        _cities.add(city.toCity())
    }

    override fun onCityUpdated(city: FBCity) {
        // Implementar se necessário
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.toCity())
    }
}

class MainViewModelFactory(private val db: FBDatabase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
