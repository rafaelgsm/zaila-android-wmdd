package com.zailaapp.zaila.views.activities

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.animateClickBounceOpen
import com.zailaapp.zaila.extensions.invis
import com.zailaapp.zaila.extensions.navigateOnce
import com.zailaapp.zaila.extensions.projectOnly.getSensorFromUri
import com.zailaapp.zaila.extensions.projectOnly.measureUserMenuClosed
import com.zailaapp.zaila.extensions.vis
import com.zailaapp.zaila.utils.PreferencesManager
import com.zailaapp.zaila.views.activities.artworkDetails.ArtworkDetailsActivity
import com.zailaapp.zaila.views.base.BaseActivity
import com.zailaapp.zaila.views.fragments.profile.ProfileFragment
import kotlinx.android.synthetic.main.layout_menu_user.*


class MainActivity : BaseActivity(R.layout.activity_main) {

    private val _navController by lazy { findNavController(R.id.main_nav_host) }
    private var currentTab = R.id.homeFragment

    companion object {

        private const val REQUEST_CODE_SCANNER = 123
        var menuHeightExpanded = -1

        fun newIntent(): Intent {
            val intent = Intent(ZailaApplication.instance, MainActivity::class.java)
            return intent
        }

        //region USER MENU ATTRS
        val BUTTON_ANIM_RESIZE = 150L

        //Persisting the values here for the sake of time:
        var isOpenUserMenu = true

        //Todo - refactor - those are being manipulated by list fragments...
        var BOTTOM_WIDTH_OPEN = -1
        var BOTTOM_HEIGHT_OPEN = -1
        var BOTTOM_WIDTH = -1
        var BOTTOM_HEIGHT = -1

        //Todo - refactor these are being used by other fragments
        var USER_MENU_WIDTH_OPEN = -1
        var USER_MENU_HEIGHT_OPEN = -1

        var USER_MENU_WIDTH_CLOSED = -1
        var USER_MENU_HEIGHT_CLOSED = -1
        //endregion USER MENU ATTRS
    }

    //region FULL_SCREEN_IMMERSIVE

    fun enterFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun exitFullScreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    //endregion FULL_SCREEN_IMMERSIVE

    //region initialization (Entry points)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.fade_in, R.anim.stay)


        //region measure user menu
        layout_menu_user.doOnPreDraw {
            USER_MENU_WIDTH_OPEN = it.measuredWidth
            USER_MENU_HEIGHT_OPEN = it.measuredHeight
        }

        measureUserMenuClosed { w, h ->
            USER_MENU_WIDTH_CLOSED = w
            USER_MENU_HEIGHT_CLOSED = h
        }
        //endregion measure user menu

        /**
         * Will run if the Activity is created for the first time.
         */
        tryInitializeFromNFC(intent)
        tryInitializeURL(intent)


        //region click listeners, menu animation setup

//        view_scan_touch.onTouchScan {
//            navigateToArtwork("n131")
//        }

        setAlphaButtons(btn_1_menu)

        btn_1_menu?.setOnClickListener {

            btn_1_menu.animateClickBounceOpen() {
                setAlphaButtons(btn_1_menu)
            }

            navigate(R.id.homeFragment)
        }

        btn_2_menu?.setOnClickListener {

            btn_2_menu.animateClickBounceOpen()

            startActivityForResult(
                ScannerActivity.newIntent("$REQUEST_CODE_SCANNER"),
                REQUEST_CODE_SCANNER
            )
        }

        btn_3_menu?.setOnClickListener {

            btn_3_menu.animateClickBounceOpen {
                setAlphaButtons(btn_3_menu)

                layout_menu_user.invis()
            }

            navigate(R.id.actionGoProfile)
        }

        btn_4_menu?.setOnClickListener {

            btn_4_menu.animateClickBounceOpen()

            navigate(R.id.actionGoArtworkList)
        }



        setupUserMenuAnimation()
        //endregion click listeners, menu animation setup

        //region load user data
        circularProgressBar.progress = 25f
        //endregion load user data

//        navigateToArtwork("n123")
    }

    private fun setAlphaButtons(buttonClicked: View) {
        val buttons = arrayOf(btn_1_menu, btn_2_menu, btn_3_menu)

        buttons.forEach {
            if (buttonClicked == it) {
                it.alpha = 1f
            } else {
                it.alpha = 0.5f
            }
        }
    }

    //region navigate action
    private fun navigate(actionId: Int) {

        /// RETURN STATEMENT HERE ///
        if (actionId == R.id.homeFragment) {

            //If user selected HOME tab, then try to pop to the top'est fragment from the HOME stack
            //(Which is the exhibition details for now)
            var popResult = _navController.popBackStack(R.id.exhibitionDetailsFragment, false)
            currentTab = R.id.exhibitionDetailsFragment

            //If the POP was not successful, then pop to the museumDetailsFragment
            if (!popResult) {
                popResult = _navController.popBackStack(R.id.museumDetailsFragment, false)
                currentTab = R.id.museumDetailsFragment

                //If the POP was not successful, then pop to the base of the HOME stack (home)
                if (!popResult) {
                    _navController.popBackStack(R.id.homeFragment, false)
                    currentTab = R.id.homeFragment
                }
            }

            return
        }
        /// RETURN STATEMENT HERE ///


        if (currentTab == actionId) {
            _navController.popBackStack(actionId, false)
        } else {
            _navController.navigateOnce(actionId)
        }

        currentTab = actionId
    }
    //endregion navigate action

    /**
     * Will run when the app is opened and the Activity is running. (foreground or background)
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        tryInitializeFromNFC(intent)
        tryInitializeURL(intent)
    }

    //endregion initialization (Entry points)

    //region URL read
    private fun tryInitializeURL(intent: Intent?) {

        if (intent?.action == Intent.ACTION_VIEW) {

            //if there is any data in the intent, then we try to get the sensorId from it:
            intent.data?.let {
                val sensorId = it.toString().getSensorFromUri()

                //If a valid sensorId exists, then we navigate to artwork
                if (sensorId != null) {
                    navigateToArtwork(sensorId)
                } else {

                    //todo - Do nothing?
                    Log.d("", "")
                }
            }

        }


    }
    //endregion URL read

    //region NFC read
    private fun tryInitializeFromNFC(intent: Intent?) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {

            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                // Process the messages array.


                messages.forEach {

                    it.records.forEach { record ->

                        //We can either read the uri:
                        val uri = record.toUri().toString() //nfs://com.zailaapp.zaila/1

                        //Or get the payload in bytes and convert to string:
//                        val payloadData = record.payload.toString(Charsets.UTF_8)

                        val sensorId = uri.getSensorFromUri()

                        sensorId?.let { id ->
                            navigateToArtwork(id)
                        }


                    } //end foreach

                }//end foreach


            }//end intent.getParcelableArrayExtra

        }//end if
    }

    //endregion NFC read

    //region navigate to artwork
    private fun navigateToArtwork(sensorId: String) {

        if (PreferencesManager.isLogged()) {
            startActivity(ArtworkDetailsActivity.newIntent(sensorId))
        } else {
            startActivity(LoginActivity.newIntent())
            finish()
        }

    }
    //endregion navigate to artwork


    //region USER MENU

    override fun onResume() {
        super.onResume()

        resetMeasurementsForUserMenu()
    }

    //Check global attributed at the companion object.

    private var constraintSetOpen: ConstraintSet? = null
    private var constraintSetClosed: ConstraintSet? = null

    /**
     * If it is open, then will close, and after that will pull pack the list items.
     * If it is closed, then will push the list items first, and later will open the menu.
     * (This is because the constraintSet animation is canceling the list item animation when run at the same time)
     */
    private fun setupUserMenuAnimation() {

        if (menuHeightExpanded < 0) {
            layout_menu_user.post {
                menuHeightExpanded = layout_menu_user.measuredHeight
            }
        }


        //Initialize the constraintSets (For animation the open/close)
        if (constraintSetOpen == null) {
            constraintSetOpen = ConstraintSet()
            constraintSetOpen!!.clone(layout_menu_user)

            constraintSetClosed = ConstraintSet()
            constraintSetClosed!!.clone(this, R.layout.layout_menu_user_closed)
        }

        //region open close user menu
        layout_image_user.setOnClickListener {

            isOpenUserMenu = !isOpenUserMenu

            if (isOpenUserMenu) {

                //For opening, we notify the children to adapt, and then open

                resetMeasurementsForUserMenu()

                Handler().postDelayed(
                    {

                        val t: Transition = ChangeBounds()
                        t.duration = 150L
                        TransitionManager.beginDelayedTransition(layout_menu_user, t)

                        val constraint =
                            if (isOpenUserMenu) constraintSetOpen else constraintSetClosed
                        constraint?.applyTo(layout_menu_user)

                    },
                    BUTTON_ANIM_RESIZE
                )


            } else {

                //for closing, we close first then notify the child to adapt:

                val t: Transition = ChangeBounds()
                t.duration = 150L
                t.addListener(object : Transition.TransitionListener {
                    override fun onTransitionEnd(transition: Transition) {

                        resetMeasurementsForUserMenu()

                    }

                    override fun onTransitionResume(transition: Transition) {}
                    override fun onTransitionPause(transition: Transition) {}
                    override fun onTransitionCancel(transition: Transition) {}
                    override fun onTransitionStart(transition: Transition) {}
                })

                TransitionManager.beginDelayedTransition(layout_menu_user, t)

                val constraint = if (isOpenUserMenu) constraintSetOpen else constraintSetClosed
                constraint?.applyTo(layout_menu_user)
            }

        }

        //endregion open close user menu
    }

    //region resetMeasurementsForUserMenu
    /**
     * Forces the current fragment to recalculate measurements, and adjust list items.
     */
    private fun resetMeasurementsForUserMenu() {

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.main_nav_host)
        val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]

        if (currentFragment is UserMenuAttachable) {
            currentFragment.calculateMeasurements(true)
            currentFragment.animateItemsBottomResize()
        }
    }

    interface UserMenuAttachable {

        fun calculateMeasurements(reset: Boolean = false): Boolean
        fun animateItemsBottomResize()
    }
    //endregion resetMeasurementsForUserMenu

    //endregion USER MENU

    //region QR code result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCANNER) {
            if (resultCode == Activity.RESULT_OK) {

                val result = data?.extras?.getString("$REQUEST_CODE_SCANNER")

                //// - Check path scheme properly
                result?.let {

                    val sensorId = result.getSensorFromUri()

                    sensorId?.let { id ->
                        navigateToArtwork(id)
                    } //context?
                } //result?

            } //end if RESULT_OK

        } //end if requestCode
    }

    //endregion QR code result

    //Show menu buttons, if invisible:
    override fun onBackPressed() {


        ////////////////////////////////////////////////////////////
        // PROFILE
        ////////////////////////////////////////////////////////////
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.main_nav_host)
        val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]

        if (currentFragment is ProfileFragment) {
            currentFragment.handleBackPress {
                //Callback: After animation is done:
                layout_menu_user.vis()
                btn_1_menu.callOnClick()
            }

            return
        }
        ////////////////////////////////////////////////////////////
        // RETURN HERE ----
        ////////////////////////////////////////////////////////////

        super.onBackPressed()
    }
}