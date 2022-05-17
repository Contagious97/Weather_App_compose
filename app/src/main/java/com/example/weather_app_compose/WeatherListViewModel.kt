package com.example.weather_app_compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherListViewModel : ViewModel(){
    var weatherList : MutableState<List<WeatherInfo>> = mutableStateOf(listOf());
    var approvedTime: MutableState<String> = mutableStateOf("");




    fun fetchWeather(longitude : Double, latitude: Double){
        viewModelScope.launch {
            val api: String;
        }
    }

}