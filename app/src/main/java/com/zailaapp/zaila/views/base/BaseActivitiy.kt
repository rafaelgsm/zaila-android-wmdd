package com.zailaapp.zaila.views.base

import android.content.pm.PackageManager
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zailaapp.zaila.R

abstract class BaseActivity(@LayoutRes layout: Int) : AppCompatActivity(layout) {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PERMISSION RELATED:
    ////////////////////////////////////////////////////////////////////////////////////////////////

    var mHasRequestedPermission = false

    var permissionListener: (Boolean) -> Unit? = {}
    var mPermissions: List<String> = ArrayList()

    /**
     * Usage:
     * hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION, etc)
     */
    fun hasPermission(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }

    /**
     * Usage:
     * requestPermissionsBase(
     *   {
     *      if (it) {
     *          //granted!
     *      } else {
     *          //Not granted!
     *      }
     *   },
     *   Manifest.permission.ACCESS_COARSE_LOCATION, etc)
     *
     *   NOTE: WILL ONLY RUN IF PERMISSION WAS NOT YET REQUESTED!!!
     */
    fun requestPermissionsBase(listener: (Boolean) -> Unit, vararg permissions: String) {

        if (mHasRequestedPermission) {
            return
        }

        mHasRequestedPermission = true

        mPermissions = permissions.asList()
        permissionListener = listener

        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionListener(hasPermission(*mPermissions.toTypedArray()))
        permissionListener = {}
    }


    //region transparent status bar
    protected fun setTransparentStatusBar(@ColorRes colorId: Int = R.color.status_bar) {

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = ContextCompat.getColor(this, colorId)
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
    //endregion transparent status bar
}