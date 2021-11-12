package com.example.wschat.utils

import kotlin.properties.Delegates

var change: Change? = null
var resultChange: String by Delegates.observable("<no result>") { _, _, new ->
    if (change != null) {
        change!!.change(new)
    }
}

fun setOnChange(onchange: Change) {
    change = onchange
}

interface Change {
    fun change(message:String)
}