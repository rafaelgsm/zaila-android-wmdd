package com.zailaapp.zaila.extensions

import android.animation.ValueAnimator


fun Int.animateTo(
    to: Int,
    totalDuration: Long,
    listenerProgress: (currentValue: Int) -> Unit
): ValueAnimator {

    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofInt(this, to)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        listenerProgress(value)
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.LinearInterpolator()
    valueAnimator.duration = totalDuration

    //6 Go!
    valueAnimator.start()

    return valueAnimator
}

fun Float.animateToAccelerateDeccelerate(
    to: Float,
    totalDuration: Long,
    listenerProgress: (currentValue: Float) -> Unit
): ValueAnimator {

    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofFloat(this, to)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Float
        listenerProgress(value)
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
    valueAnimator.duration = totalDuration

    //6 Go!
    valueAnimator.start()

    return valueAnimator
}

fun Float.getValueBased(currentValue: Int): Int {
    return (currentValue * this.toDouble()).toInt()
}