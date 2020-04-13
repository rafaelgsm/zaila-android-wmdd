package com.zailaapp.zaila.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

const val TAG_FADE_IN = "TAG_FADE_IN"
const val TAG_FADE_OUT = "TAG_FADE_OUT"

/**
 * Can be called multiple times.
 * Will trigger just once, and then again only when the animation finishes.
 *
 * Uses the View's TAG to track the animation "TAG_FADE_OUT"
 */
fun View.animateFadeOutTagAnim(duration: Long, listener: () -> Unit = {}) {

    //Uses TAG to see if its not animating already:
    if (tag == TAG_FADE_OUT) return

    tag = TAG_FADE_OUT

    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofFloat(alpha - 0.01f, 0f)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Float
        alpha = value
    }

    valueAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)

            //clears anim tag:
            tag = ""
            listener()
        }
    })

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.LinearInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}

/**
 * Can be called multiple times.
 * Will trigger just once, and then again only when the animation finishes.
 *
 * Uses the View's TAG to track the animation "TAG_FADE_OUT"
 */
fun View.animateFadeInTagAnim(duration: Long, listener: () -> Unit = {}) {

    //Uses TAG to see if its not animating already:
    if (tag == TAG_FADE_IN) return

    tag = TAG_FADE_IN

    vis()

    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofFloat(alpha, 1f)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Float
        alpha = value
    }

    valueAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)

            //clears anim tag:
            tag = ""
            listener()
        }
    })

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.LinearInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}