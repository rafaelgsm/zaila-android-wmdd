package com.zailaapp.zaila.views.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.animatePaddingStart
import com.zailaapp.zaila.extensions.decelerateInterpolation
import com.zailaapp.zaila.views.activities.MainActivity
import com.zailaapp.zaila.views.activities.MainActivity.Companion.BOTTOM_HEIGHT
import com.zailaapp.zaila.views.activities.MainActivity.Companion.BOTTOM_HEIGHT_OPEN
import com.zailaapp.zaila.views.activities.MainActivity.Companion.BOTTOM_WIDTH
import com.zailaapp.zaila.views.activities.MainActivity.Companion.BOTTOM_WIDTH_OPEN
import kotlinx.android.synthetic.main.layout_menu_user.*

/**
 * Highly coupled with the MainActivity.
 */
abstract class BaseListResizableFragment(@LayoutRes layout: Int) : BaseFragment(layout),
    MainActivity.UserMenuAttachable {

    //region abstract methods
    abstract fun getRecyclerView(): RecyclerView?

    abstract fun getLinearLayoutManager(): LinearLayoutManager?

    abstract fun getAdapterResizable(): AdapterResizable?

    abstract fun getPushRateMenuOpen(): Double

    abstract fun getPushRateMenuClosed(): Double

    abstract fun getInterpolatorFactor(): Double

    //endregion abstract methods

    //region public methods
    protected fun getMenuHeight(): Int {

        return MainActivity.menuHeightExpanded
    }

    /**
     * Call this after building the RecyclerView and adapter
     */
    protected fun setupScrollAnimation() {

        getRecyclerView()?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                //////// GUARD STATEMENT ///////////
                val isAllMeasured = calculateMeasurements()
                if (!isAllMeasured) {
                    return
                }
                //////// RETURN HERE ///////////


                //region push bottom items
                //Iterate over all visible views, and check their position:
                forEachItemProportion { proportion: Double, currentWidth: Int, item: View ->

                    //Set the item list padding:
                    item.setPadding(currentWidth, 0, 0, 0)

                    getAdapterResizable()?.adjustItem(
                        proportion,
                        currentWidth,
                        item
                    )
                }
                //endregion push bottom items
            }
        })

    }
    //endregion public methods

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        //If menu was closed some place else, then adjust the list
        calculateMeasurements(true)
        animateItemsBottomResize()
    }

    ////////////////////////////////////////////////////////////////////////////////
    //region scroll animation setup
    ////////////////////////////////////////////////////////////////////////////////

    private var PUSH_RATE = 1.0 //Item will be fully pushed upon reaching 50% of bottom view
        get() = if (MainActivity.isOpenUserMenu) getPushRateMenuOpen() else getPushRateMenuClosed()

    private var INTERPOLATOR_FACTOR = getInterpolatorFactor()

    private var LIST_HEIGHT = -1

    //After these pixels that we start shrinking:
    private val THRESHOLD
        get() = LIST_HEIGHT - BOTTOM_HEIGHT

    override fun calculateMeasurements(reset: Boolean): Boolean {

        if (reset) {
            LIST_HEIGHT = -1
            BOTTOM_WIDTH = -1
            BOTTOM_HEIGHT = -1
        }

        if (BOTTOM_WIDTH <= 0) {
            BOTTOM_WIDTH = (activity as MainActivity).layout_menu_user.width

            if (BOTTOM_WIDTH_OPEN <= 0) {
                BOTTOM_WIDTH_OPEN = BOTTOM_WIDTH
            }

            BOTTOM_WIDTH = if (MainActivity.isOpenUserMenu) BOTTOM_WIDTH_OPEN else BOTTOM_WIDTH
        }

        if (BOTTOM_WIDTH <= 0) {
            return false
        }

        if (BOTTOM_HEIGHT <= 0) {
            BOTTOM_HEIGHT =
                (activity as MainActivity).layout_menu_user.height

            if (BOTTOM_HEIGHT_OPEN <= 0) {
                BOTTOM_HEIGHT_OPEN = BOTTOM_HEIGHT
            }

            BOTTOM_HEIGHT =
                if (MainActivity.isOpenUserMenu)
                    BOTTOM_HEIGHT_OPEN
                else
                    BOTTOM_HEIGHT

        }

        if (BOTTOM_HEIGHT <= 0) {
            return false
        }

        if (LIST_HEIGHT <= 0) {
            LIST_HEIGHT = getRecyclerView()?.height ?: -1
        }

        if (LIST_HEIGHT <= 0) {
            return false
        }

        return true
    }

    //region push bottom items (Open menu)
    override fun animateItemsBottomResize() {

        forEachItemProportion { proportion: Double, currentWidth: Int, item: View ->

            item.animatePaddingStart(currentWidth, MainActivity.BUTTON_ANIM_RESIZE)

            getAdapterResizable()?.adjustItem(
                proportion,
                currentWidth,
                item
            )
        }

    }
    //endregion push bottom items (Open menu)

    //endregion scroll animation setup
    ////////////////////////////////////////////////////////////////////////////////


    //region forEachItemProportion

    /**
     * Iterate over the list of items, and for each item will return the proportion,
     * Based on how further the item has passed the threshold (0.0 to 1.0).
     *
     * Will return -1 if item has not reached the threshold.
     *
     * With this we can adjust the item padding, image size, etc.
     */
    private fun forEachItemProportion(listener: (proportion: Double, currentWidth: Int, item: View) -> Unit) {

        /// GUARD STATEMENT ////
        val llm = getLinearLayoutManager() ?: return
        //// RETURN HERE ////

        //Iterate over all visible views, and check their position:
        for (i in 0..100) {

            val lastVisibleItem =
                llm.findViewByPosition(llm.findLastVisibleItemPosition() - i)

            if (lastVisibleItem == null) break

            //todo - check children
            val row_item_resizable =
                lastVisibleItem.findViewById<View>(R.id.row_item_resizable)

            val THRESHOLD_BOTTOM_OF_ITEM = THRESHOLD - lastVisibleItem.height

            //This will indicate if the bottom of the list item has touched the threshold:
            val x =
                lastVisibleItem.top - THRESHOLD_BOTTOM_OF_ITEM

            if (x > 0) {

                //passed the THRESHOLD:

                //This will give us a percentage from 0 to 1:
                var valueIWant = x / (BOTTOM_HEIGHT * PUSH_RATE).toDouble()

                valueIWant = if (valueIWant > 1) 1.0 else valueIWant

                //DecelerateInterpolator:
                valueIWant = valueIWant.decelerateInterpolation(INTERPOLATOR_FACTOR)
                //...

                val resultPadding = (valueIWant * BOTTOM_WIDTH)

                listener(valueIWant, resultPadding.toInt(), row_item_resizable)

            } else {

                //not passed the threshold:

                listener((-1).toDouble(), 0, row_item_resizable)
            }
        }
    }
    //endregion forEachItemProportion
}

interface AdapterResizable {
    fun adjustItem(proportion: Double, currentWidth: Int, item: View)
}