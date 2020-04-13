package com.zailaapp.zaila.extensions

import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun ImageView.setColor(@ColorRes colorResId: Int) {
    setColorFilter(ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_ATOP)
}