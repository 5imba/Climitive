package com.wildraion.climitive.network.remote.model

class City(
    var id: String,
    var name: String,
    var coord: Coord,
    var country: String,
    var population: Long,
    var timezone: Int,
    var sunrise: Long,
    var sunset: Long
)