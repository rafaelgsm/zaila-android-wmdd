package com.zailaapp.zaila.views.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView

/**
 * ImageView that draws itself with rounded corners.
 * -Not rly optimized-
 */
class RoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val radius = 30F

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        val clipPath = Path()

        clipPath.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            radius,
            radius,
            Path.Direction.CW
        )
        canvas?.clipPath(clipPath)

        super.onDraw(canvas)
    }

}