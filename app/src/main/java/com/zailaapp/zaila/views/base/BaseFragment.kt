package com.zailaapp.zaila.views.base

import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zailaapp.zaila.R

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    //Check out the discussions of whether this is a leak or an issue with the design of the Fragment API:
//    https://github.com/square/leakcanary/issues/1656
//    https://issuetracker.google.com/issues/145468285
    private var currentView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (currentView == null) {
            currentView = super.onCreateView(inflater, container, savedInstanceState)
        }

        return currentView
    }

    /**
     * Resets audio control to default.
     */
    override fun onResume() {
        super.onResume()

        activity?.volumeControlStream = AudioManager.STREAM_SYSTEM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTransparentStatusBar()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        setTransparentStatusBar()
    }

    protected fun setTransparentStatusBar() {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        activity?.window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        activity?.window?.statusBarColor =
            ContextCompat.getColor(activity?.applicationContext!!, R.color.status_bar_transparent)
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = activity?.window
        val winParams = win?.attributes

        winParams?.let {
            if (on) {
                it.flags = it.flags or bits
            } else {
                it.flags = it.flags and bits.inv()
            }
            win.attributes = winParams
        }

    }


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

        activity?.let { currentActivity ->

            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        currentActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }

        } //end activity?.let


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

        activity?.let { currentActivity ->
            ActivityCompat.requestPermissions(currentActivity, permissions, 1)
        }

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
}