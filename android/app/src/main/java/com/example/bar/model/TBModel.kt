package com.example.bar.model

import android.text.Editable

class TBModel(
    name: String,
    x: String,
    y: String,
    z: String
) {
    var name: String? = name
    var x: String? = x
    var y: String? = y
    var z: String? = z

    override fun toString(): String {
        return "name : [$name][$x, $y, $z]"
    }
}