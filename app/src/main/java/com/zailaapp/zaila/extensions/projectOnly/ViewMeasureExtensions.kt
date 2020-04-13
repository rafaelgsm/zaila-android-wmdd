package com.zailaapp.zaila.extensions.projectOnly

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.doOnPreDraw
import com.zailaapp.zaila.R
import com.zailaapp.zaila.model.responses.Exhibition
import com.zailaapp.zaila.model.responses.Museum
import kotlinx.android.synthetic.main.row_exhibition.view.*


/**
 * Determines the height of a museum item in pixels.
 * Basically inflates the view in the background with mocking data, and return its height.
 */
@SuppressLint("ResourceType")
fun Activity.measureMuseumRow(callback: (height: Int) -> Unit) {

    val content: ViewGroup = findViewById(android.R.id.content)

    val v: View = layoutInflater.inflate(R.layout.row_museum, content, true)
        .findViewById(R.id.row_item_resizable)
    v.fillMuseum(Museum.mock())

    v.doOnPreDraw {
        callback(v.measuredHeight)
        content.removeView(v)
    }
}

fun Activity.measureTopExhibitionRow(callback: (heightHeader: Int, heightToCenter: Int) -> Unit) {

    val content: ViewGroup = findViewById(android.R.id.content)

    val v: View = layoutInflater.inflate(R.layout.row_exhibition, content, true)
        .findViewById(R.id.row_item_resizable)

    v.fillExhibition(Exhibition.mock())


    v.doOnPreDraw {
        callback(v.tv_title_exhibition.measuredHeight, v.tv_title_exhibition_bg.measuredHeight)
        content.removeView(v)
    }
}

//region user menu
/**
 * Get measurements for closed user menu layout.
 * Basically adds the layout to the screen, measures it, and removes it right before drawing it.
 */
fun Activity.measureUserMenuClosed(callback: (width: Int, height: Int) -> Unit) {

    val content: ViewGroup = findViewById(android.R.id.content)

    val v = layoutInflater.inflate(R.layout.layout_menu_user_closed, content, true)
        .findViewWithTag<View>("layout_menu_user_closed")

//    v.fillUser(Museum.mock())

    v.doOnPreDraw {
        callback(v.measuredWidth, v.measuredHeight)
        content.removeView(v)
    }
}
//endregion user menu

fun View.forceMeasure() {
    if (measuredWidth <= 0 || measuredHeight <= 0) {
        if (layoutParams.width <= 0 || layoutParams.height <= 0) {

            if (layoutParams.width > 0) {
                val widthSpec =
                    View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY)
                measure(widthSpec, View.MeasureSpec.UNSPECIFIED)
            } else {
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            }
        } else {
            val widthSpec =
                View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY)
            val heightSpec =
                View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
            measure(widthSpec, heightSpec)
        }
    }
}

/**
 * Inflates a view using the default content ViewGroup as a parent.
 */
private fun Activity.createViewFromLayout(@LayoutRes layoutRes: Int): View {
    val content: ViewGroup = findViewById(android.R.id.content)
    return layoutInflater.inflate(layoutRes, content, false)
}