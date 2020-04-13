package com.zailaapp.zaila.extensions

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager

/**
 * Created by rafaelmontenegro on 12/04/2018.
 */
/**
 * Apply a constraint bottom to the view, to match other's view TOP.
 * NOTE: The view must have a ConstraintLayout as a parent!
 */
fun View.changeConstraintBotTopOf(view: View, margin: Int = 0) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(id, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, margin)
    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.changeConstraintTopTopOf(view: View) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(id, ConstraintSet.TOP, view.id, ConstraintSet.TOP, 0)
    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.changeConstraintTopBotOf(view: View) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, 0)
    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.changeConstraintBotBotOf(view: View, margin: Int = 0) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(id, ConstraintSet.BOTTOM, view.id, ConstraintSet.BOTTOM, 0)
    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.changeConstraintEndEndOfParent() {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(
        id,
        ConstraintSet.END,
        (parent as ConstraintLayout).id,
        ConstraintSet.END,
        0
    )
    constraintSet.applyTo(parent as ConstraintLayout)
}

/**
 * NOTE: The view must have a ConstraintLayout as a parent!
 */
fun View.removeConstraintTop() {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)

    constraintSet.clear(id, ConstraintSet.TOP)

    constraintSet.applyTo(parent as ConstraintLayout)
}

/**
 * NOTE: The view must have a ConstraintLayout as a parent!
 */
fun View.removeConstraintBot() {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)

    constraintSet.clear(id, ConstraintSet.BOTTOM)

    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.centerConstraintsInView(view: View, margin: Int = 0) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)

    constraintSet.connect(id, ConstraintSet.BOTTOM, view.id, ConstraintSet.BOTTOM, 0)
    constraintSet.connect(id, ConstraintSet.END, view.id, ConstraintSet.END, 0)
    constraintSet.connect(id, ConstraintSet.START, view.id, ConstraintSet.START, 0)
    constraintSet.connect(id, ConstraintSet.TOP, view.id, ConstraintSet.TOP, 0)
    constraintSet.setVerticalBias(id, 0.5f)

    constraintSet.applyTo(parent as ConstraintLayout)
}

/**
 * Considers the constraint layout is the parent and occupies the whole screen!
 */
fun Guideline.moveGuidelineOverView(target: View, duration: Long) {
    val constraintLayout = parent as ConstraintLayout
    var ratio =
        (constraintLayout.height.toFloat() - target.height.toFloat()) / constraintLayout.height.toFloat()

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.setGuidelinePercent(id, ratio)

    val autoTransition = AutoTransition()
    autoTransition.setDuration(duration)

    TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)

    constraintSet.applyTo(constraintLayout)
}

fun Guideline.moveGuidelineTo(ratio: Float, duration: Long) {
    val constraintLayout = parent as ConstraintLayout

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.setGuidelinePercent(id, ratio)

    val autoTransition = AutoTransition()
    autoTransition.setDuration(duration)

    TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)

    constraintSet.applyTo(constraintLayout)
}

fun Guideline.moveGuidelineTo(ratio: Float) {
    val constraintLayout = parent as ConstraintLayout

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.setGuidelinePercent(id, ratio)

    constraintSet.applyTo(constraintLayout)
}

fun View.changeWidthPercent(ratio: Float) {
    val constraintLayout = parent as ConstraintLayout

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.constrainPercentWidth(id, ratio)

    constraintSet.applyTo(constraintLayout)
}


/**
 * Considers the constraint layout is the parent and occupies the whole screen!
 */
fun Guideline.moveGuidelineBelowView(target: View, duration: Long) {
    val constraintLayout = parent as ConstraintLayout
    var ratio = (target.bottom.toFloat()) / constraintLayout.height.toFloat()

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.setGuidelinePercent(id, ratio)

    val autoTransition = AutoTransition()
    autoTransition.setDuration(duration)

    TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)

    constraintSet.applyTo(constraintLayout)
}

fun View.verticalBias(bias: Float) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.setVerticalBias(id, bias)
    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.horizontalBias(bias: Float) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.setHorizontalBias(id, bias)
    constraintSet.applyTo(parent as ConstraintLayout)
}

/**
 * Can be called multiple times.
 * Will trigger just once, and then again only when the animation finishes.
 *
 * Uses the View's TAG to track the animation "bias-animation"
 */
fun View.horizontalBiasAnimation(bias: Float, duration: Long = 200L) {

    //Uses TAG to see if its not animating already:
    if (tag == "bias-animation") return

    tag = "bias-animation"

    val constraintLayout = parent as ConstraintLayout

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.setHorizontalBias(id, bias)

    val autoTransition = AutoTransition()
    autoTransition.setDuration(duration)
    autoTransition.addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            tag = ""
        }

        override fun onTransitionResume(transition: Transition) {
            //...
        }

        override fun onTransitionPause(transition: Transition) {
            //...
        }

        override fun onTransitionCancel(transition: Transition) {
            tag = ""
        }

        override fun onTransitionStart(transition: Transition) {
            //...
        }

    })


    TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)

    constraintSet.applyTo(parent as ConstraintLayout)
}

