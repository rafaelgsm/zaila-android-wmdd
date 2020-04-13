package com.zailaapp.zaila.extensions

import android.app.Activity
import android.graphics.Point

/**
 * Created by rafaelmontenegro on 12/04/2018.
 */
fun Activity.getScreenHeight(): Int {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    val height = size.y

    return height
}

/**
 * Retrieves the FULLSCREEN height.
 * (Some devices will hide the back-home-overview buttons and occupy their space)
 */
fun Activity.getScreenHeightReal(): Int {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getRealSize(size)
    val height = size.y

    return height
}

fun Activity.getScreenWidth(): Int {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    val width = size.x

    return width
}