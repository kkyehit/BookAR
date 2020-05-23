package com.example.bar.model

import android.text.Editable

class TBModel(
    name: String,
    x: String,
    y: String,
    z: String,
    floor: String
) {
    var name: String? = name
    var x: String? = x
    var y: String? = y
    var z: String? = z
    var floor: String? = floor

    override fun toString(): String {
        return "name : [$name][$x, $y, $z]"
    }
}