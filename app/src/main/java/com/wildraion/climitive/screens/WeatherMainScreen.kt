package com.wildraion.climitive.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wildraion.climitive.MainViewModel
import com.wildraion.climitive.common.Utils
import com.wildraion.climitive.network.remote.model.WeatherModel
import com.wildraion.climitive.R

@Composable
fun WeatherMainScreen(viewModel: MainViewModel) {
    val weatherModel by viewModel.weatherData.observeAsState()
    val state by viewModel.state.observeAsState()

    when (state) {
        MainViewModel.State.Loading -> LoadingScreen()
        MainViewModel.State.Loaded -> WeatherForecastView(weatherModel)
        else -> NoInternetScreen()
    }
}

@Composable
fun WeatherForecastView(weatherModel: WeatherModel?) {
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
        Column {
            Row(
                modifier = Modifier.padding(20.dp)
            ) {
                Icon(Icons.Filled.LocationOn,null)
                Text(
                    text = weatherModel.city.name,
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = Utils.temperatureToPretty(currentWeather.main.temp),
                        style = MaterialTheme.typography.h3
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = currentWeather.weather[0].main,
                        style = MaterialTheme.typography.h1
                    )
                }
                Image(painter = painterResource(
                    id = context.resources
                        .getIdentifier(
                            "ic_${currentWeather.weather[0].icon}",
                            "drawable",
                            context.packageName
                        )
                ),
                    contentDescription = context.getString(R.string.weather_icon),
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
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
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun WindHumidityView(weatherModel: WeatherModel) {
    val currentWeather = weatherModel.list[0]
    val context = LocalContext.current

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
                Text(text = context.getString(R.string.wind))
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.speed))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.wind.speed.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.degree))
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
                Text(text = context.getString(R.string.humidity))
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.level))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.main.humidity.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.pressure))
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
    val context = LocalContext.current

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
                Text(text = context.getString(R.string.clouds))
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.percentage))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.clouds.all.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.description))
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
                Text(text = context.getString(R.string.temperatures))
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.minimal))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = currentWeather.main.temp_min.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.maximal))
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
            contentDescription = context.getString(R.string.weather_icon),
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