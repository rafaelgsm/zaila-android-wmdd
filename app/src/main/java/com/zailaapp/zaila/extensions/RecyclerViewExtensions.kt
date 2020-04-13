package com.zailaapp.zaila.extensions

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import me.everything.android.ui.overscroll.adapters.ViewPagerOverScrollDecorAdapter

fun RecyclerView.addSingleDecoration(decoration: RecyclerView.ItemDecoration) {
    while (getItemDecorationCount() > 0) {
        removeItemDecorationAt(0)
    }

    addItemDecoration(decoration)
}


/**
 * Sets default bouncy decorator for the recyclerview.
 * Also returns the decorator so tou can apply listeners to whatever actions you might wish.
 */
fun RecyclerView.setBouncyDecor(): VerticalOverScrollBounceEffectDecorator {
    return VerticalOverScrollBounceEffectDecorator(
        RecyclerViewOverScrollDecorAdapter(this),
        VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD,
        VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
        -1f // Default is -2
    )
}

fun RecyclerView.setBouncyDecorHorizontal(): HorizontalOverScrollBounceEffectDecorator {
    return HorizontalOverScrollBounceEffectDecorator(
        RecyclerViewOverScrollDecorAdapter(this),
        HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD,
        HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
        -1f // Default is -2
    )
}


fun ViewPager.setBouncyDecor(): HorizontalOverScrollBounceEffectDecorator {
    return HorizontalOverScrollBounceEffectDecorator(
        ViewPagerOverScrollDecorAdapter(this),
        HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD,
        HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
        -1f // Default is -2
    )
}

fun RecyclerView.disableTouch() {

    addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}


    })

}

