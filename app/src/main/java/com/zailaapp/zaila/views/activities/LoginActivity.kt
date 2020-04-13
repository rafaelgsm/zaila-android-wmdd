package com.zailaapp.zaila.views.activities

import android.content.Intent
import android.os.Bundle
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.views.base.BaseActivity

class LoginActivity : BaseActivity(R.layout.activity_login) {

    companion object {

        fun newIntent(): Intent {
            val intent = Intent(ZailaApplication.instance, LoginActivity::class.java)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransparentStatusBar()

    }
}