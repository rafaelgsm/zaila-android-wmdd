package com.zailaapp.zaila.views.fragments.exhibitionDetails

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.animatePaddingBottom
import com.zailaapp.zaila.extensions.projectOnly.fillExhibition
import com.zailaapp.zaila.extensions.projectOnly.forceMeasure
import com.zailaapp.zaila.model.responses.Exhibition
import com.zailaapp.zaila.utils.JsonProvider
import com.zailaapp.zaila.views.activities.MainActivity
import com.zailaapp.zaila.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_exhibition_details.*
import kotlinx.android.synthetic.main.layout_menu_user.*
import kotlinx.android.synthetic.main.layout_menu_zaila.*

class ExhibitionDetailsFragment : BaseFragment(R.layout.fragment_exhibition_details),
    MainActivity.UserMenuAttachable {

    private val args by navArgs<ExhibitionDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region initial padding
        //Sets padding bottom of the layout based on whether the user menu is open/closed:
        fragment_exhibition_details?.setPadding(
            0, 0, 0,
            if (MainActivity.isOpenUserMenu) MainActivity.USER_MENU_HEIGHT_OPEN else MainActivity.USER_MENU_HEIGHT_CLOSED
        )
        //endregion initial padding

        val exhibition: Exhibition = JsonProvider.fromJson(args.exhibition)
        fragment_exhibition_details?.fillExhibition(exhibition, true)


        //region click listeners
        btn_1_menu_zaila?.setOnClickListener { }
        btn_2_menu_zaila?.setOnClickListener { }
        btn_3_menu_zaila?.setOnClickListener { }
        //endregion click listeners


        hideScrollTrackCheck()
        setupZailaMenuAnimation()

        //Closes zaila menu if user menu is open:
        if (MainActivity.isOpenUserMenu) {
            (requireActivity() as MainActivity).layout_image_user?.callOnClick()
        } else {
            shrink()
        }

    }

    //region hide scroll track check
    private fun hideScrollTrackCheck() {
        scrollbar_desc_exhibition?.forceMeasure()

        tv_desc_exhibition?.post {


            val parentHeight = scrollbar_desc_exhibition?.measuredHeight
            val childHeight = tv_desc_exhibition?.measuredHeight

            if (parentHeight != null && childHeight != null) {
                scrollbar_desc_exhibition?.isVerticalScrollBarEnabled = parentHeight < childHeight
            }
        }
    }
    //endregion hide scroll track check

    //region contract user menu padding bottom

    /**
     * Use this call to calculate your measurements, if you have any:
     */
    override fun calculateMeasurements(reset: Boolean): Boolean {
        //todo...
        return true
    }

    /**
     * Use this to stretch/shrink the layout:
     */
    override fun animateItemsBottomResize() {

        if (MainActivity.isOpenUserMenu || isOpenZailaMenu) {
            shrink()
        } else {
            stretch()
        }

        if (MainActivity.isOpenUserMenu) {
            if (isOpenZailaMenu) {
                layout_menu_zaila?.callOnClick()
            }
        }

        hideScrollTrackCheck()
    }

    private fun shrink() {
        fragment_exhibition_details?.animatePaddingBottom(
            MainActivity.USER_MENU_HEIGHT_OPEN,
            MainActivity.BUTTON_ANIM_RESIZE
        )
    }

    private fun stretch() {
        fragment_exhibition_details?.animatePaddingBottom(
            MainActivity.USER_MENU_HEIGHT_CLOSED,
            MainActivity.BUTTON_ANIM_RESIZE
        )
    }
    //endregion contract user menu padding bottom

    //region ZAILA MENU

    private var isOpenZailaMenu = true

    private var constraintSetOpen: ConstraintSet? = null
    private var constraintSetClosed: ConstraintSet? = null

    private fun setupZailaMenuAnimation() {

        //Initialize the constraintSets (For animation the open/close)
        if (constraintSetOpen == null) {
            constraintSetOpen = ConstraintSet()
            constraintSetOpen!!.clone(layout_menu_zaila)

            constraintSetClosed = ConstraintSet()
            constraintSetClosed!!.clone(context, R.layout.layout_menu_zaila_closed)
        }

        //region open close user menu
        layout_menu_zaila?.setOnClickListener {

            isOpenZailaMenu = !isOpenZailaMenu

            if (isOpenZailaMenu) {

                //For opening, we notify the children to adapt, and then open

                if (MainActivity.isOpenUserMenu) {
                    (requireActivity() as MainActivity).layout_image_user?.callOnClick()
                } else {
                    animateItemsBottomResize()
                }

                Handler().postDelayed(
                    {

                        if (layout_menu_zaila == null) return@postDelayed

                        val t: Transition = ChangeBounds()
                        t.duration = MainActivity.BUTTON_ANIM_RESIZE
                        TransitionManager.beginDelayedTransition(layout_menu_zaila, t)

                        val constraint =
                            if (isOpenZailaMenu) constraintSetOpen else constraintSetClosed
                        constraint?.applyTo(layout_menu_zaila)

                    },
                    MainActivity.BUTTON_ANIM_RESIZE
                )


            } else {

                if (layout_menu_zaila == null) return@setOnClickListener

                //for closing, we close first then notify the child to adapt:

                val t: Transition = ChangeBounds()
                t.duration = MainActivity.BUTTON_ANIM_RESIZE
                t.addListener(object : Transition.TransitionListener {
                    override fun onTransitionEnd(transition: Transition) {

                        animateItemsBottomResize()
                    }

                    override fun onTransitionResume(transition: Transition) {}
                    override fun onTransitionPause(transition: Transition) {}
                    override fun onTransitionCancel(transition: Transition) {}
                    override fun onTransitionStart(transition: Transition) {}
                })

                TransitionManager.beginDelayedTransition(layout_menu_zaila, t)

                val constraint = if (isOpenZailaMenu) constraintSetOpen else constraintSetClosed
                constraint?.applyTo(layout_menu_zaila)
            }

        }

        //endregion open close user menu
    }

    //endregion ZAILA MENU
}
