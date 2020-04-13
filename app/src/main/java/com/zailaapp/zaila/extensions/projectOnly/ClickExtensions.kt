package com.zailaapp.zaila.extensions.projectOnly

import android.os.Handler
import android.view.MotionEvent
import android.view.View

private val _handler = Handler()
private var _handlerRunnable: Runnable? = null

fun View.onTouchScan(callback: () -> Unit) {

    setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            if (event?.action == MotionEvent.ACTION_DOWN) {

                _handler.removeCallbacks(_handlerRunnable)

                _handlerRunnable = Runnable {

                    _handler.removeCallbacks(_handlerRunnable)
                    callback()
                }

                _handler.postDelayed(_handlerRunnable, 4000)

                return v?.onTouchEvent(event) ?: true
            }

            if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
                _handler.removeCallbacks(_handlerRunnable)
            }

            return v?.onTouchEvent(event) ?: true
        }
    })
}