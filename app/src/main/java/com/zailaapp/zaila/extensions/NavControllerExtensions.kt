package com.zailaapp.zaila.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.navigateOnce(directions: NavDirections) {
    try {
        navigate(directions)
    } catch (e: IllegalArgumentException) {
        //directions unknown to this _navController
        //Happens if called .navigate multiple times, in this case, by clicking 2 items at the same time
    }
}

fun NavController.navigateOnce(@IdRes resId: Int) {
    try {
        navigate(resId)
    } catch (e: IllegalArgumentException) {
        //directions unknown to this _navController
        //Happens if called .navigate multiple times, in this case, by clicking 2 items at the same time
    }
}