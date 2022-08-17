package com.wildraion.climitive

import android.location.Location
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wildraion.climitive.common.default
import com.wildraion.climitive.network.GeoManager
import com.wildraion.climitive.network.NetworkConnectionManager
import com.wildraion.climitive.network.remote.WeatherApi
import com.wildraion.climitive.network.remote.model.WeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherState {
    class DefaultState : WeatherState()
    class LoadingState : WeatherState()
    class SuccessState : WeatherState()
    class ErrorState<T>(val message: T) : WeatherState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherApi: WeatherApi,
    private val geoManager: GeoManager,
    private val networkConnectionManager: NetworkConnectionManager
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherModel>()
    val weatherData: LiveData<WeatherModel> = _weather

    private val _state = MutableLiveData<WeatherState>()
        .default(initialData = WeatherState.DefaultState())
    val state: LiveData<WeatherState> = _state

    private var weatherRequestJob: Job? = null

    fun observeNetworkConnection(activity: ComponentActivity) {
        networkConnectionManager.observe(activity) { isNetworkAvailable: Boolean ->
            if (isNetworkAvailable && _state.value is WeatherState.ErrorState<*>) {
                fetchLocation(activity)
            } else if (!isNetworkAvailable) {
                onError(message = activity.getString(R.string.no_internet_connection))
            }
        }
    }

    fun fetchLocation(activity: ComponentActivity) {
        geoManager.fetchLocation(activity = activity) { location ->
            prepareRequest(location = location)
        }
    }

    private fun prepareRequest(location: Location?) {
        // Get system language and units
        val lang = Locale.current.language
        val units = when(lang) {
            "us", "lr", "mm" -> "imperial"
            else -> "metric"
        }
        val lat: String
        val lon: String
        if (location != null) {
            lat = location.latitude.toString()
            lon = location.longitude.toString()
        } else {
            // If no Location use London location
            lat = "51.51650199277461"
            lon = "-0.12913951336584356"
        }

        fetchWeather(
            lat = lat,
            lon = lon,
            units = units,
            lang = lang
        )
    }

    private fun fetchWeather(
        lat: String,
        lon: String,
        units: String,
        lang: String
    ) {
        weatherRequestJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                _state.postValue(WeatherState.LoadingState())

                val response = weatherApi.getCurrentWeather(
                    lat = lat,
                    lon = lon,
                    appid = "69804d11c802801608e08fe1f8144146",
                    units = units,
                    lang = lang)

                if (response.isSuccessful) {
                    _weather.postValue(response.body())
                    _state.postValue(WeatherState.SuccessState())
                } else {
                    onError(message = response.message())
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { onError(it) }
            }
        }
    }

    private fun onError(message: String) {
        Log.e("GetWeatherRequest",
            "Exception during request -> $message")
        _state.postValue(WeatherState.ErrorState(message = message))
    }

    override fun onCleared() {
        weatherRequestJob?.cancel()
        geoManager.getLocationTaskCancellation()
        super.onCleared()
    }
}