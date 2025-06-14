package com.example.weatherapp.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.City

class MainViewModel : ViewModel() {

    private val _cities = initialCities().toMutableStateList()
    val cities: List<City>
        get() = _cities.toList()

    fun remove(city: City) {
        _cities.remove(city)
    }

    fun add(name: String) {
        _cities.add(City(name = name))
    }

    private fun initialCities() = List(20) { i ->
        City(name = "Cidade $i", weather = "Carregando clima...")
    }
}
