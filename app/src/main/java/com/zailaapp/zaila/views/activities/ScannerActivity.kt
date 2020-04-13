package com.zailaapp.zaila.views.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.afterMeasured
import com.zailaapp.zaila.extensions.setWidthHeight
import com.zailaapp.zaila.extensions.vis
import com.zailaapp.zaila.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity : BaseActivity(R.layout.activity_scanner), ZXingScannerView.ResultHandler {

    companion object {


        /**
         * Give any value to the "extra_key".
         * Use it to retrieve the QR value in the activity result:
         *
         * val resultString = data?.extras?.getString(your_extra_key)
         */
        fun newIntent(extra_key: String): Intent {
            val intent = Intent(ZailaApplication.instance, ScannerActivity::class.java)

            intent.putExtra("extra_key", extra_key)

            return intent
        }
    }

    private var mScannerView: ZXingScannerView? = null
    private var cameraStarted: Boolean = false

    private val _extra_key by lazy { intent.getStringExtra("extra_key") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.fade_in_fast, R.anim.stay)

        //...

        layout_scanner_bg?.setOnClickListener {
            finish()
        }

        camera_view?.afterMeasured {
            camera_view?.setWidthHeight(camera_view.width, camera_view.width)
        }

        camera_view?.forceLayout()

    }

    private fun initCamera() {
        mScannerView = ZXingScannerView(this)

        mScannerView!!.setLaserEnabled(false)

        mScannerView!!.setShouldScaleToFill(true)

        mScannerView!!.setIsBorderCornerRounded(false)
        mScannerView!!.setMaskColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.transparent
            )
        )
        mScannerView!!.setBorderColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.transparent
            )
        )
        mScannerView!!.setBorderLineLength(-1)
        mScannerView!!.setBorderStrokeWidth(-1)
        mScannerView!!.setBorderCornerRadius(-1)
        camera_view.addView(mScannerView)

        mScannerView!!.flash = false

        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
        cameraStarted = true

        //We are forcing th the camera preview View, to resize itself as soon as it loads.
        //Because it is being drawn outside its parent's bounds, when the background is transparent.
        MainScope().launch {

            while (true) {
                delay(100)

                if (mScannerView!!.childCount >= 2) {
                    val cameraPreviewView = mScannerView!!.getChildAt(0)

                    if (cameraPreviewView != null) {
                        camera_view.vis()
                        cameraPreviewView.setWidthHeight(camera_view.width, camera_view.width)
                        break
                    }
                }//end if
            }//end while
        }//end coroutine
    }

    /**
     * Check permissions and resets the camera scanner onResume:
     */
    override fun onResume() {
        super.onResume()

        if (hasPermission(Manifest.permission.CAMERA)) {

            if (cameraStarted) {
                camera_view.removeAllViews()
            }

            initCamera()

        } else {

            //........
            requestPermissionsBase(
                {
                    if (it) {

                        //granted!
                        initCamera()

                    } else {

                        //todo-- dialog warning the need of the camera?
                        finish()

                    }
                },
                Manifest.permission.CAMERA
            )
            //.......

        }
    }

    /**
     * Stops the camera onStop:
     */
    override fun onStop() {
        super.onStop()


        mScannerView?.stopCamera()
    }

    //region scanner result
    override fun handleResult(result: Result?) {

        result!!.text?.let {

            val resultIntent = Intent()
            resultIntent.putExtra(_extra_key, it)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
    //endregion scanner result

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.stay, R.anim.fade_out_fast)
    }
}
