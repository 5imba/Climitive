package com.wildraion.climitive

import android.location.Location
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wildraion.climitive.network.GeoManager
import com.wildraion.climitive.network.remote.WeatherApi
import com.wildraion.climitive.network.remote.model.WeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherApi: WeatherApi,
    private val geoManager: GeoManager
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherModel>()
    val weatherData: LiveData<WeatherModel> = _weather

    private val _state = MutableLiveData(State.Loading)
    val state: LiveData<State> = _state

    private var weatherRequestJob: Job? = null

    fun setNetworkState(isNetworkAvailable: Boolean, activity: ComponentActivity) {
        if (isNetworkAvailable) {
            if (_state.value != State.Loaded) {
                fetchWeatherForecast(activity)
            }
        } else {
            _state.postValue(State.Error)
        }
    }

    fun fetchWeatherForecast(context: ComponentActivity) {
        fetchLocationAndLoadForecast(context)
    }

    private fun fetchLocationAndLoadForecast(activity: ComponentActivity) {
        geoManager.geoCallbackListener = object : GeoManager.GeoCallbackListener {
            override fun onSuccessListener(location: Location?) {
                if (location != null) {
                    // Get system language and units
                    val country = Locale.current.language
                    val units = when(country) {
                        "us", "lr", "mm" -> "imperial"
                        else -> "metric"
                    }
                    // Request forecast
                    fetchWeather(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        units,
                        country
                    )
                }
            }
        }
        geoManager.fetchLocation(activity)
    }

    private fun fetchWeather(
        lat: String,
        lon: String,
        units: String,
        lang: String
    ) {
        weatherRequestJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                _state.postValue(State.Loading)

                val response = weatherApi.getCurrentWeather(
                    lat,
                    lon,
                    "69804d11c802801608e08fe1f8144146",
                    units,
                    lang)

                if (response.isSuccessful) {
                    _weather.postValue(response.body())
                    _state.postValue(State.Loaded)
                } else {
                    onError(response.message())
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { onError(it) }
            }
        }
    }

    private fun onError(message: String) {
        Log.e("GetWeatherRequest",
            "Exception during request -> $message")
        _state.postValue(State.Error)
    }

    override fun onCleared() {
        weatherRequestJob?.cancel()
        geoManager.getLocationTaskCancellation()

        super.onCleared()
    }

    enum class State {
        Loading,
        Loaded,
        Error
    }
}