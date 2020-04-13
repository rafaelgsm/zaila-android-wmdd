package com.zailaapp.zaila.extensions

import android.content.res.Resources

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pixelToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
