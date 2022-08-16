package com.wildraion.climitive.network.remote.model

data class WeatherModel(
    var cod: Int,
    var message: Int,
    var cnt: Int,
    var list: kotlin.collections.List<List>,
    var city: City
)