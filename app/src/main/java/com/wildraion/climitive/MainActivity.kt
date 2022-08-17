package com.wildraion.climitive

import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import com.wildraion.climitive.network.GeoManager
import com.wildraion.climitive.network.NetworkConnectionManager
import com.wildraion.climitive.screens.WeatherMainScreen
import com.wildraion.climitive.ui.theme.ClimitiveTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity()  {

    @Inject
    lateinit var geoManager: GeoManager

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geoManager.geoCallbackListener = object : GeoManager.GeoCallbackListener {
            override fun onSuccessListener(location: Location?) {
                viewModel.fetchWeatherForecast(location)
            }
        }
        geoManager.fetchLocation(this)

        networkConnectionManager.observe(this) { isNetworkAvailable ->
            viewModel.setNetworkState(isNetworkAvailable)
        }

        setContent {
            ClimitiveTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherMainScreen(viewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        geoManager.getLocationTaskCancellation()
    }
}







