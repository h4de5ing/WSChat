package com.example.wschat.ext

import java.text.SimpleDateFormat
import java.util.*

fun Long.date(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(this))