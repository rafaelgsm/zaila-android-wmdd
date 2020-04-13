package com.zailaapp.zaila.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * If you are going to use requestfocus then do it AFTER using this method!!!
 */
fun EditText.openKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInputFromWindow(
        applicationWindowToken,
        InputMethodManager.SHOW_IMPLICIT, 0
    )

    if (text.toString().isNotEmpty()) {
        setSelection(text.toString().length)
    }

}

fun EditText.closeKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * editText.afterTextChanged { doSomethingWithText(it) }
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            removeTextChangedListener(this)
            afterTextChanged.invoke(editable.toString())
            addTextChangedListener(this)
        }
    })
}

/**
 * Runs after text changed after a delay, and only ONCE, after the delay has passed.
 */
private val typingThrottleHandler = Handler()
private var typingThrottleRunnable: Runnable? = null

fun EditText.afterTextChangedDelay(afterTextChanged: (String) -> Unit): TextWatcher {

    val tw = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            typingThrottleHandler.removeCallbacks(typingThrottleRunnable)

            typingThrottleRunnable = Runnable {

                afterTextChanged.invoke(editable.toString())
            }

            typingThrottleHandler.postDelayed(typingThrottleRunnable, 500)
        }
    }

    this.addTextChangedListener(tw)

    return tw
}

fun EditText.onDone(onDoneButtonClicked: () -> Unit) {
    this.setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                onDoneButtonClicked.invoke()
            }

            return false
        }
    })
}

fun EditText.onNext(onNextButtonClicked: () -> Unit) {
    this.setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                onNextButtonClicked.invoke()
            }

            return false
        }
    })
}

fun EditText.onSelectChangeIconColor(
    @ColorRes originalColor: Int, @ColorRes selectedColor: Int, image: ImageView
) {

    this.afterFocusChanged {

        //gets the icon:
        val currentIcon = image.drawable
//            if (compoundDrawables.isNotEmpty()) {
//                compoundDrawables.filterNotNull().first()
//
//            } else {
//                compoundDrawablesRelative.filterNotNull().first()
//            }

        val newColor = if (it) selectedColor else originalColor

        //Changes color of the icon:
        currentIcon.mutate()
            .setColorFilter(
                ContextCompat.getColor(context, newColor),
                PorterDuff.Mode.SRC_ATOP    //Override the color
            )

        //replaces icon:
        image.setImageDrawable(currentIcon)
//        setCompoundDrawables(null, null, currentIcon, null)

    }
}

/////////////////////////////////////////////
// VALIDATION:
/////////////////////////////////////////////

fun EditText.isValidEmail(): Boolean {
    val REGEX_EMAIL = ".+@.+\\.[a-z]+"

    return !TextUtils.isEmpty(text.toString().trim()) && text.toString().trim().matches(REGEX_EMAIL.toRegex())
}

fun EditText.isValidPassword(): Boolean {
    val REGEX_PASSWORD_LIMIT = ".{4,}$"

    return !TextUtils.isEmpty(text.toString()) && text.toString().matches(REGEX_PASSWORD_LIMIT.toRegex())
}