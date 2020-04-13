package com.zailaapp.zaila.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt

/**
 * Adds new behavior to Views so they can provide a clean-optimized way to obtain
 * its data. (Basically you can call the whole OnGlobalLayoutListener in a single line at your activity)
 *
 * Ex.:
 * view.afterMeasured{
 *  //Here I can access the BaseView's data easily!!!
 * }
 */

//AFTER MEASURE ONLY WORKS IF WIDTH AND HEIGHT ARE ON WRAP CONTENT OR FIXED DP SIZE!!!!
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

inline fun <T : View> T.afterMeasuredTop(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {

            if (top > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

inline fun <T : View> T.afterMeasuredWidth(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

inline fun <T : View> T.afterMeasuredHeight(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (height > 0 || measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun View.afterFocusChanged(afterFocusChanged: (Boolean) -> Unit) {
    onFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            afterFocusChanged(hasFocus)
        }

    }
}

/**
 * Listeners for DOWN and up+cancel
 * (Used for animation extensions and all)
 */
fun View.onTouchDownUpCancel(down: () -> Unit, upCancel: () -> Unit) {
    setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                down()
                return v?.onTouchEvent(event) ?: true
            }

            if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
                upCancel()
            }

            return v?.onTouchEvent(event) ?: true
        }
    })
}

fun View.setMarginTop(top: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(p.marginStart, top, p.marginEnd, p.bottomMargin)
        requestLayout()
    }


}

fun View.setMargins(left: Int, top: Int, right: Int, bot: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bot)
        requestLayout()
    }


}

fun View.setWidth(width: Int) {
    layoutParams.width = width
    requestLayout()
}

fun View.setHeight(height: Int) {
    layoutParams.height = height
    requestLayout()
}

fun View.setWidthHeight(width: Int, height: Int) {
    layoutParams.height = height
    layoutParams.width = width
    requestLayout()
}

//Returns a bitmap for the view:
fun View.snapshot(@ColorInt backgroundColor: Int?): Bitmap {
    if (measuredWidth <= 0 || measuredHeight <= 0) {
        if (layoutParams.width <= 0 || layoutParams.height <= 0) {

            if (layoutParams.width > 0) {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY)
                measure(widthSpec, View.MeasureSpec.UNSPECIFIED)
            } else {
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            }
        } else {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
            measure(widthSpec, heightSpec)
        }
    }
    val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    if (backgroundColor != null) {
        canvas.drawColor(backgroundColor)
    }
    if (left <= 0 || top <= 0 || right <= 0 || bottom <= 0) {
        layout(0, 0, measuredWidth, measuredHeight)
    }
    draw(canvas)
    return bitmap
}