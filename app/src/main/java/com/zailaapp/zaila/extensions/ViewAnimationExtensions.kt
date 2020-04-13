package com.zailaapp.zaila.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.zailaapp.zaila.R
import kotlin.math.cos
import kotlin.math.pow


fun View.animateY(from: Float, to: Float, duration: Long) {
    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofFloat(from, to.toFloat())

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Float
        translationY = value
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}

/**
 * Imageview... But could also be used on any other view...
 * The difference here is that we are using the ArgbEvaluator class to change some color...
 */
fun ImageView.animateChangeColor(
    colorFrom: Int,
    colorTo: Int,
    duration: Long,
    listener: () -> Unit = {}
) {

    val f = ContextCompat.getColor(context, colorFrom)
    val t = ContextCompat.getColor(context, colorTo)

    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), f, t)

    colorAnimation.addUpdateListener { animator ->
        setColorFilter(animator.animatedValue as Int, PorterDuff.Mode.SRC_ATOP)
    }

    colorAnimation.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            listener()
        }
    })

    colorAnimation.interpolator = android.view.animation.LinearInterpolator()
    colorAnimation.duration = duration

    colorAnimation.start()
}

//listenerUpdate returns the color, so update the view with this value.
fun View.animateChangeColor(
    colorFrom: Int,
    colorTo: Int,
    duration: Long,
    listenerUpdate: (color: Int) -> Unit,
    listenerEnd: () -> Unit = {}
) {

    val f = ContextCompat.getColor(context, colorFrom)
    val t = ContextCompat.getColor(context, colorTo)

    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), f, t)

    colorAnimation.addUpdateListener { animator ->
        listenerUpdate(animator.animatedValue as Int)
    }

    colorAnimation.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            listenerEnd()
        }
    })

    colorAnimation.interpolator = android.view.animation.LinearInterpolator()
    colorAnimation.duration = duration

    colorAnimation.start()
}


fun View.animatePaddingStart(to: Int, duration: Long) {
    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofInt(paddingStart, to)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        setPadding(value, 0, 0, 0)
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}

fun View.animatePaddingBottom(to: Int, duration: Long) {
    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofInt(paddingBottom, to)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        setPadding(0, 0, 0, value)
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}

fun View.animateSize(to: Int, duration: Long) {
    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofInt(width, to)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.height = value
        layoutParams.width = value
        requestLayout()
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}

fun View.animateHeight(to: Int, duration: Long) {
    //1 start from below the screen, and stop at the top:
    val valueAnimator = android.animation.ValueAnimator.ofInt(height, to)

    //2	While animating also translate the view by setting the translation to the value of the animation:
    valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.height = value
        requestLayout()
    }

    //5 Set up the animator’s duration and interpolator:
    valueAnimator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
    valueAnimator.duration = duration

    //6 Go!
    valueAnimator.start()
}

fun View.revealCircular() {
    // get the center for the clipping circle
    val cx = width / 2
    val cy = height / 2

    // get the final radius for the clipping circle
    val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

    // create the animator for this view (the start radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
    // make the view visible and start the animation
    visibility = View.VISIBLE
    anim.start()
}

fun View.hideCircular(callback: () -> Unit) {
    // get the center for the clipping circle
    val cx = width / 2
    val cy = height / 2

    // get the final radius for the clipping circle
    val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

    // create the animator for this view (the start radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, finalRadius, 0f)

    anim.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            invis()
            callback()
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {}

    })

    // make the view visible and start the animation
    visibility = View.VISIBLE
    anim.start()
}


//Custom animations:

fun View.animateClickBounce(listener: () -> Unit = {}) {
    val shrink = AnimationUtils.loadAnimation(context, R.anim.shrink)

    shrink.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {

            listener()

            //bounce:
            val bounce = AnimationUtils.loadAnimation(context, R.anim.bounce)

            bounce.interpolator = object : BounceInterpolator() {
                private val mAmplitude = 0.2
                private val mFrequency = 20

                override fun getInterpolation(time: Float): Float {

                    return (-1 * Math.E.pow(-time / mAmplitude) *
                            cos(mFrequency * time) + 1).toFloat()
                }
            }

            startAnimation(bounce)
        }

        override fun onAnimationStart(animation: Animation?) {}

    })

    startAnimation(shrink)
}

fun View.animateClickBounceOpen(listener: () -> Unit = {}) {
    val grow = AnimationUtils.loadAnimation(context, R.anim.grow)

    grow.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {

            listener()

            //bounce:
            val bounce = AnimationUtils.loadAnimation(context, R.anim.bounce_from_1)

            bounce.interpolator = object : BounceInterpolator() {
                private val mAmplitude = 0.2
                private val mFrequency = 15

                override fun getInterpolation(time: Float): Float {

                    return (-1 * Math.E.pow(-time / mAmplitude) *
                            cos(mFrequency * time) + 1).toFloat()
                }
            }

            startAnimation(bounce)
        }

        override fun onAnimationStart(animation: Animation?) {}

    })

    startAnimation(grow)
}


