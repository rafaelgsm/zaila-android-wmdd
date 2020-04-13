package com.zailaapp.zaila.extensions.projectOnly

import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginStart
import androidx.transition.ArcMotion
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.hideCircular
import com.zailaapp.zaila.extensions.invis
import com.zailaapp.zaila.extensions.revealCircular


fun View.moveUserIcon() {
    val l = parent as ConstraintLayout

    val progress_profile_menu_container = l.findViewById<View>(R.id.progress_profile_menu_container)
    val layout_image_user = l.findViewById<View>(R.id.layout_image_user)

    val layout_user_level = l.findViewById<View>(R.id.layout_user_level)
    val circularProgressBar = l.findViewById<View>(R.id.circularProgressBar)


    val constraintSet = ConstraintSet()
    constraintSet.clone(parent as ConstraintLayout)

    constraintSet.connect(
        id,
        ConstraintSet.TOP,
        progress_profile_menu_container.id,
        ConstraintSet.TOP,
        resources.getDimensionPixelSize(R.dimen.margin_top_menu_profile)
    )
    constraintSet.connect(
        id,
        ConstraintSet.BOTTOM,
        progress_profile_menu_container.id,
        ConstraintSet.BOTTOM,
        resources.getDimensionPixelSize(R.dimen.margin_bottom_menu_profile_plus_10)
    )
    constraintSet.connect(
        id,
        ConstraintSet.END,
        progress_profile_menu_container.id,
        ConstraintSet.END,
        layout_image_user.marginStart
    )
    constraintSet.connect(
        id,
        ConstraintSet.START,
        progress_profile_menu_container.id,
        ConstraintSet.START,
        0
    )

    addTransition {
        progress_profile_menu_container.revealCircular()
        layout_user_level.invis()
        circularProgressBar.invis()
    }

    constraintSet.applyTo(parent as ConstraintLayout)
}

fun View.moveUserIconBack(listener: () -> Unit) {

    val l = parent as ConstraintLayout
    val progress_profile_menu_container = l.findViewById<View>(R.id.progress_profile_menu_container)

    progress_profile_menu_container.hideCircular {

        val constraintSet = ConstraintSet()
        constraintSet.clone(parent as ConstraintLayout)

        constraintSet.clear(id, ConstraintSet.TOP)
        constraintSet.clear(id, ConstraintSet.END)

        constraintSet.connect(
            id,
            ConstraintSet.BOTTOM,
            l.id,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.connect(
            id,
            ConstraintSet.START,
            l.id,
            ConstraintSet.START,
            0
        )

        addTransition {
            listener()
        }

        constraintSet.applyTo(parent as ConstraintLayout)
    }
}

private fun View.addTransition(listenerEnd: () -> Unit = {}) {

    val constraintLayout = parent as ConstraintLayout

    val autoTransition = AutoTransition()

    val motion = ArcMotion()
    motion.minimumHorizontalAngle = 70f
    motion.minimumVerticalAngle = 70f
    autoTransition.setPathMotion(motion)

    autoTransition.interpolator = DecelerateInterpolator()
    autoTransition.duration = 300L

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