@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.zailaapp.zaila.extensions

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.zailaapp.zaila.R


/**
 * Created by rafaelmontenegro on 31/03/18.
 */

fun TextView.setClickableSpan(text: String, listener: () -> Unit) {

}


/**
 * Receives a listener and a list of strings and configure the following spans:
 *
 * 1. For each text String, will apply a click listener. (The same listener for every String. Check the position param for validation)
 * 2. Based on the list of strings, will apply an alternate typeface for each one (Even and odds)
 * 3. It assumes the TextView has only ONE color for the whole text.
 *
 * NOTE: For older apis, we must put the font file in the assets folder. For newer apis we can use de font-on-resources implementation.
 */

class CustomSpanSetup(
    var colorMap: HashMap<Int, Int> = HashMap(), //ColorRes and positions to be painted
    var boldPositions: ArrayList<Int> = ArrayList() //List of text positions to be set to bold
)


/**
 * Usage:
 *
 * someTextView.setCustomFontAndColor(
 * CustomSpanSetup().apply {
 * colorMap = hashMapOf(0 to R.color.colorAccent)  //Hashmap that indicates the first position has the color X
 * boldPositions = arrayListOf(0)  //Bold positions
 * },
 * {
 *  //Text click it -> position
 * },
 * text1,
 * text2,
 * text3, ...etc
 * )
 */
fun TextView.setCustomFontAndColor(
    spanSetup: CustomSpanSetup,
    listenerSpanClick: ((position: Int) -> Unit),
    vararg textArray: String
) {

    //Defines the 2 typefaces to be used. (Change the fonts here as needed.)
//    var typeface1 = ResourcesCompat.getFont(context, R.font.ubuntu_regular)
//
//    var typeface2 = ResourcesCompat.getFont(context, R.font.ubuntu_bold)


    //Defines the 2 typefaces to be used. (Change the fonts here as needed.)
    val typeface1 = ResourcesCompat.getFont(context, R.font.opensans_regular)

    val typeface2 = ResourcesCompat.getFont(context, R.font.opensans_bold)

    var fullText = ""

    for (s in textArray) {
        fullText += s
    }

    // Create a new spannable:
    val ss = SpannableString(fullText)

    //For each text in the list, will apply the spans:
    for (i in textArray.indices) {
        val s = textArray[i]
        val start = fullText.indexOf(s)
        val end = fullText.indexOf(s) + (s).length

        //Typeface to be used in the current iteration:
        val span = if (!spanSetup.boldPositions.contains(i)) CustomTypefaceSpan(
            "A" + i,
            typeface1!!
        ) else CustomTypefaceSpan("A" + i, typeface2!!)

        ss.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        //Click listener for each text part - With the position clicked in its params:
        val clickableSpan = object : ClickableSpan() {

            override fun onClick(widget: android.view.View) {
                listenerSpanClick(i)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val fcs = ForegroundColorSpan(
            if (spanSetup.colorMap.contains(i)) ContextCompat.getColor(
                context,
                spanSetup.colorMap[i]!!
            ) else currentTextColor
        )
        ss.setSpan(fcs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    }

    text = ss

    //Must use this to keep the original color of the text:
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

/**
 * Custom span class used in this extension file!
 */
private class CustomTypefaceSpan(family: String, private val newType: Typeface) :
    TypefaceSpan(family) {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        if (old == null) {
            oldStyle = 0
        } else {
            oldStyle = old.style
        }

        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }

        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }

        paint.typeface = tf
    }
}