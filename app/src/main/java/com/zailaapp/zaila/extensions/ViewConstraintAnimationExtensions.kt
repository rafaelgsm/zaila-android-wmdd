package com.zailaapp.zaila.extensions

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager

fun View.moveConstraintTopTopOf(view: View, marginTopValue: Int, listenerEnd: () -> Unit = {}) {

    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(id, ConstraintSet.TOP, view.id, ConstraintSet.TOP, marginTopValue)

    addTransition(listenerEnd)

    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.moveConstraintTopBotOf(view: View, marginTopValue: Int, listenerEnd: () -> Unit = {}) {

    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)
    constraintSet.connect(id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, marginTopValue)

    addTransition(listenerEnd)

    constraintSet.applyTo(parent as ConstraintLayout)
}


private fun View.addTransition(listenerEnd: () -> Unit = {}) {

    val constraintLayout = parent as ConstraintLayout

    val autoTransition = AutoTransition()
    autoTransition.duration = 200L

    autoTransition.addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            listenerEnd()
        }

        override fun onTransitionResume(transition: Transition) {}

        override fun onTransitionPause(transition: Transition) {}

        override fun onTransitionCancel(transition: Transition) {}

        override fun onTransitionStart(transition: Transition) {}
    })
    TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
}