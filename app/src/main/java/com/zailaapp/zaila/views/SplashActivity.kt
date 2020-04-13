package com.zailaapp.zaila.views

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.zailaapp.zaila.R
import com.zailaapp.zaila.utils.PreferencesManager
import com.zailaapp.zaila.views.activities.LoginActivity
import com.zailaapp.zaila.views.activities.MainActivity

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            if (PreferencesManager.isLogged()) {
                startActivity(MainActivity.newIntent())
            } else {
                startActivity(LoginActivity.newIntent())
            }

            finish()

        }, 1800)

    }
}
