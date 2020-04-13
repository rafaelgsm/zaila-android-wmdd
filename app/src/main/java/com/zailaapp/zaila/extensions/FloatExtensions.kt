package com.zailaapp.zaila.extensions


fun Float.getValueBasedOnProportion(min: Float, max: Float): Float {
    return (max - min) * this + min
}

fun Double.getValueBasedOnProportion(min: Float, max: Float): Float {
    return (max - min) * this.toFloat() + min
}