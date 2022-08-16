package com.wildraion.climitive.network.remote.model

import kotlin.collections.List

class List(
    var dt: Long,
    var main: Main,
    var weather: List<Weather>,
    var clouds: Clouds,
    var wind: Wind,
    var visibility: Long,
    var pop: Float,
    var sys: Sys,
    var dt_txt: String
)