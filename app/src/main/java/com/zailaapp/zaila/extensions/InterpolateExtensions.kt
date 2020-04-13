package com.zailaapp.zaila.extensions

fun Double.decelerateInterpolation(INTERPOLATOR_FACTOR: Double): Double {
    return (1.0f - Math.pow(
        (1.0f - this),
        2 * INTERPOLATOR_FACTOR
    ))
}

fun Float.accelerateInterpolation(): Float {
    return (Math.cos((this + 1) * Math.PI) / 2.0f).toFloat() + 0.5f
}