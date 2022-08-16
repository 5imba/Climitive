package com.wildraion.climitive

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wildraion.climitive.common.Utils
import com.wildraion.climitive.network.GeoManager
import com.wildraion.climitive.network.remote.model.WeatherModel
import com.wildraion.climitive.ui.theme.ClimitiveTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var geoManager: GeoManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onStart() {
        super.onStart()
        viewModel.fetchWeatherForecast(this)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ClimitiveTheme {
            WeatherMainScreen(viewModel)
        }
    }
}

@Composable
fun WeatherMainScreen(viewModel: MainViewModel) {
    val weatherModel by viewModel.weatherData.observeAsState()
    val state by viewModel.state.observeAsState()

    when (state) {
        MainViewModel.State.Loading -> LoadingScreen()
        MainViewModel.State.Loaded ->WeatherForecastScreen(weatherModel)
        else -> NoInternetScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NoInternetScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_no_internet_svg),
            contentDescription = "No Internet")
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No Internet",
            style = MaterialTheme.typography.h2
        )
    }
}

@Composable
fun WeatherForecastScreen(weatherModel: WeatherModel?) {
    if (weatherModel != null) {
        LazyColumn(
            content = {
                item { CurrentBlockView(weatherModel) }
                item { WindHumidityView(weatherModel) }
                item { CloudsTemperaturesView(weatherModel) }
            },
            modifier = Modifier.fillMaxWidth())
    }
}



@SuppressLint("DiscouragedApi")
@Composable
fun CurrentBlockView(weatherModel: WeatherModel) {
    val currentWeather = weatherModel.list[0]
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(2),
        color = Color(0xFF88C2F7),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row {
                Icon(Icons.Filled.LocationOn,null)
                Text(
                    text = weatherModel.city.name,
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(painter = painterResource(
                    id = context.resources
                        .getIdentifier(
                            "ic_${currentWeather.weather[0].icon}",
                            "drawable",
                            context.packageName
                        )
                ),
                    contentDescription = "Weather Image",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    text = Utils.temperatureToPretty(currentWeather.main.temp),
                    style = MaterialTheme.typography.h3
                )
            }
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = currentWeather.weather[0].main,
                style = MaterialTheme.typography.h1
            )
            Spacer(modifier = Modifier.height(50.dp))
            LazyRow {
                weatherModel.list.forEach {
                    item {
                        HourlyForecastView(
                            time = Utils.parseTimeFromData(it.dt_txt),
                            imageName = it.weather[0].icon,
                            temperature = Utils.temperatureToPretty(it.main.temp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WindHumidityView(weatherModel: WeatherModel) {
    val currentWeather = weatherModel.list[0]

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(
            start = 20.dp,
            end = 20.dp
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .padding(end = 10.dp),
            shape = RoundedCornerShape(5),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Wind")
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Speed")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.wind.speed.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Degree")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.wind.deg.toString())
                }
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            shape = RoundedCornerShape(5),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Humidity")
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Level")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.main.humidity.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pressure")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.main.pressure.toString())
                }
            }
        }
    }
}

@Composable
fun CloudsTemperaturesView(weatherModel: WeatherModel) {
    val currentWeather = weatherModel.list[0]

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(20.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .padding(end = 10.dp),
            shape = RoundedCornerShape(5),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Clouds")
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Percentage")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.clouds.all.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Description")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = currentWeather.weather[0].description)
                }
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            shape = RoundedCornerShape(5),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Temperatures")
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Minimal")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.main.temp_min.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Maximal")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.main.temp_max.toString())
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun HourlyForecastView(time: String, imageName: String, temperature: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(5.dp))
        Image(
            painter = painterResource(
                id = context.resources
                    .getIdentifier(
                        "ic_$imageName",
                        "drawable",
                        context.packageName
                    )
            ),
            contentDescription = "Weather Image",
            modifier = Modifier
                .size(50.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = temperature,
            style = MaterialTheme.typography.body1
        )
    }
}

