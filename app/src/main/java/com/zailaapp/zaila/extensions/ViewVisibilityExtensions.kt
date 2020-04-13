package com.zailaapp.zaila.extensions

import android.view.View

/**
 * Created by rafaelmontenegro on 22/05/2018.
 */
fun View.vis() {
    visibility = View.VISIBLE
}

fun View.invis() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.isVis(): Boolean {
    return visibility == View.VISIBLE
}

fun View.vis(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}