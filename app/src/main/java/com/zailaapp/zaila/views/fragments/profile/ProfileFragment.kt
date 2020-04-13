package com.zailaapp.zaila.views.fragments.profile

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.animateToAccelerateDeccelerate
import com.zailaapp.zaila.extensions.disableTouch
import com.zailaapp.zaila.extensions.pixelToDp
import com.zailaapp.zaila.extensions.projectOnly.*
import com.zailaapp.zaila.extensions.setColor
import com.zailaapp.zaila.model.UserZaila
import com.zailaapp.zaila.utils.PreferencesManager
import com.zailaapp.zaila.views.base.BaseFragment
import com.zailaapp.zaila.views.custom.CenterLayoutManager
import com.zailaapp.zaila.views.fragments.profile.adapters.ProfileHorizontalRoundAdapter
import com.zailaapp.zaila.views.fragments.profile.adapters.ProfileMenuAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_profile_menu.*


class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private var pagerAdapterProfile: ProfileMenuAdapter? = null

    private val _user: UserZaila = PreferencesManager.getUser()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo - load user name and picture:
        tv_user_name_profile.text = "" + _user.name

        //region profile menu setup
        progress_profile_menu?.doOnPreDraw {
            progress_profile_menu?.post {

                if (progress_profile_menu != null) {

                    //Adjust progress bar width so we can make the progress look like the golden selection
                    progress_profile_menu.progressBarWidth =
                        (progress_profile_menu.height.pixelToDp.toFloat() / 2f)

                    //todo
                    //User image is a % of the menu size:
//                    layout_image_user_profile?.setWidthHeight(
//                        (progress_profile_menu.height * 0.34).toInt(),
//                        (progress_profile_menu.height * 0.34).toInt()
//                    )

                    //region padding icons
                    val paddingIcons_large =
                        progress_profile_menu.height / 5 //the bigger the lower the padding
                    val paddingIcons_short = progress_profile_menu.height / 7

                    btn_1_profile.setPadding(
                        paddingIcons_large,
                        paddingIcons_large,
                        paddingIcons_short,
                        paddingIcons_short
                    )

                    btn_2_profile.setPadding(
                        paddingIcons_short,
                        paddingIcons_large,
                        paddingIcons_large,
                        paddingIcons_short
                    )

                    btn_3_profile.setPadding(
                        paddingIcons_short,
                        paddingIcons_short,
                        paddingIcons_large,
                        paddingIcons_large
                    )

                    btn_4_profile.setPadding(
                        paddingIcons_large,
                        paddingIcons_short,
                        paddingIcons_short,
                        paddingIcons_large
                    )

                    //endregion padding icons

                    //region animate user icon

                    layout_image_user_profile.moveUserIcon()

                    //endregion animate user icon

                }

                setupPager()
            }
        }

        setupProfileClickListeners()
        setAngleOnTouchMenu()

        //endregion profile menu setup
    }

    //region menu animation

    //region get angle for selection vice versa
    private fun Int.getAngleForSelection(): Float {
        return when (this) {
            1 -> 269f
            2 -> 359f
            3 -> 89f
            4 -> 179f
            else -> { // Note the block
                369f
            }
        }
    }

    //Angle here is the angleStart -> -45f
    private fun Float.getSelectionForAngle(): Int {

        var x = this + 45f

        if (x > 360f) {
            x -= 360f
        }

        return when (x) {
            in 269f..360f -> 1
            in 0f..90f -> 2
            in 90f..180f -> 3
            in 180f..269f -> 4
            else -> { // Note the block
                1
            }
        }
    }
    //endregion get angle for selection vice versa

    private var isAnimating = false

    private fun animateFromToAngle(
        currentAngle: Float,
        newAngleTouch: Float,
        checkAnimating: Boolean = false
    ) {

        if (checkAnimating && isAnimating) return

        var prevAngle = currentAngle
        var newAngle = newAngleTouch

        var duration = 200L

        isAnimating = true

        if ((360f + newAngle) - prevAngle < 180) {

            newAngle += 360f

        } else {

            if (newAngle - prevAngle > 180) {
                newAngle = 0 - (360f - newAngle)
            }
        }

        prevAngle.animateToAccelerateDeccelerate(
            newAngle,
            duration
        ) {

            progress_profile_menu.startAngle = it

            if (it == newAngle) {
                isAnimating = false
            }
        }
    }
    //endregion menu animation

    //region menu select while move finger
    private fun checkAngleSelectMenu(angle: Float) {

        currentSelection = angle.getSelectionForAngle()
    }
    //endregion menu select while move finger


    //region menu button state config

    //Changes in the menu, horizontal scroll and TABS occur as this value is changed:
    private var currentSelection = 1
        set(value) {

            val prevValue = field
            field = value

            if (currentSelection != prevValue) {

                clearButtonColors()

//                animateMenu(prevValue)
                selectItemForHorizontalListMenu()

                setSelectedButtonColors()

                selectTabBasedOnCurrentPosition()
            }
        }

    private fun clearButtonColors() {
        btn_1_profile.setColor(R.color.blue)
        btn_2_profile.setColor(R.color.blue)
        btn_3_profile.setColor(R.color.blue)
        btn_4_profile.setColor(R.color.blue)
    }

    private fun setSelectedButtonColors() {
        when (currentSelection) {
            1 -> btn_1_profile.setColor(R.color.colorAccent)
            2 -> btn_2_profile.setColor(R.color.colorAccent)
            3 -> btn_3_profile.setColor(R.color.colorAccent)
            4 -> btn_4_profile.setColor(R.color.colorAccent)
            else -> {
                //...
            }
        }
    }

    private fun setupProfileClickListeners() {
        //todo
//        btn_1_profile.setOnClickListener {
//            currentSelection = 1
//        }
//
//        btn_2_profile.setOnClickListener {
//            currentSelection = 2
//        }
//
//        btn_3_profile.setOnClickListener {
//            currentSelection = 3
//        }
//
//        btn_4_profile.setOnClickListener {
//            currentSelection = 4
//        }
    }

    //endregion menu button state config

    //region Pager and Horizontal adapter setup
    private fun setupPager() {

        val llm = CenterLayoutManager(context)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        recyclerview_horizontal_profile.layoutManager = llm

        recyclerview_horizontal_profile.disableTouch()

        val adapterHorizontalRound = ProfileHorizontalRoundAdapter(
            ProfileMenuAdapter.tabs,
            recyclerview_horizontal_profile
        ) {
            //Item click
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        recyclerview_horizontal_profile.adapter = adapterHorizontalRound

        viewPager_profile.offscreenPageLimit = 4 //So wont recreate views.
        pagerAdapterProfile = ProfileMenuAdapter(context!!)
        viewPager_profile.adapter = pagerAdapterProfile

        viewPager_profile.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                initializeAllTabData()
            }

        })

        recyclerview_horizontal_profile.post {

            recyclerview_horizontal_profile.scrollToPosition(
                Integer.MAX_VALUE / 2
            )

            recyclerview_horizontal_profile.doOnNextLayout { selectItemForHorizontalListMenu() }
        }

        //To force the selected listener to trigger and populate the tabs:
        viewPager_profile.post {
            viewPager_profile.currentItem = 1
            selectTabBasedOnCurrentPosition()
        }

    }

    //region selectTabBasedOnCurrentPosition
    private fun selectTabBasedOnCurrentPosition() {
        viewPager_profile.currentItem = currentSelection - 1
    }
    //endregion selectTabBasedOnCurrentPosition
    //endregion Pager and Horizontal adapter setup


    //region horizontal scroll menu config

    private fun selectItemForHorizontalListMenu() {
        val llm = recyclerview_horizontal_profile.layoutManager as CenterLayoutManager

        val position = llm.findFirstCompletelyVisibleItemPosition()
        var count = 0

        while ((position + count) % ProfileMenuAdapter.tabs.size != currentSelection - 1) {
            count++
        }

        recyclerview_horizontal_profile.smoothScrollToPosition(position + count)
    }

    //endregion horizontal scroll menu config

    //region menu angle ON TOUCH
    private fun setAngleOnTouchMenu() {

        progress_profile_menu.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (v != null && event != null) {

                    //If its not a click, then proceed to animate/control the menu:
                    val angleStart = v.getAngleStart(event)

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            animateFromToAngle(
                                currentSelection.getAngleForSelection(),
                                angleStart.getSelectionForAngle().getAngleForSelection()
                            )
                            checkAngleSelectMenu(angleStart)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            progress_profile_menu.startAngle = angleStart
                            checkAngleSelectMenu(angleStart)

                        }

                        else -> { // cancel

                            animateFromToAngle(
                                angleStart,
                                currentSelection.getAngleForSelection(),
                                true
                            )
                        }
                    }

                }


                return true
            }

        })
    }

    //If using with ConstraintLayout 0dp w/h, then have the view start its position fom 0,0 of the screen!!!
    private fun View.getAngleStart(event: MotionEvent): Float {

        val centerX = x + width / 2;
        val centerY = y + height / 2;

        var angle = Math.toDegrees(
            Math.atan2(
                centerY.toDouble() - event.rawY,
                centerX.toDouble() - event.rawX
            )
        ).toFloat()
        if (angle < 0) {
            angle += 360f
        }

        angle -= 135f   //Correction for the starting point

        //Adjust to range between 0 to 360 angle:
        if (angle < 0) {
            angle = 360f + angle
        }

        return angle
    }
    //endregion menu angle ON TOUCH

    var hasInit = false

    //region INITIALIZE ALL TAB DATA
    private fun initializeAllTabData() {
        //region settings -> set button widths

        if (hasInit) return

        layout_profile?.findViewWithTag<View>("tab_layout_profile_settings")
            ?.fillProfileSettings(requireActivity())

        layout_profile?.findViewWithTag<View>("tab_layout_profile_zaila")
            ?.fillProfileZaila(requireActivity())

        layout_profile?.findViewWithTag<View>("tab_layout_profile_badges")
            ?.fillProfileBadges(requireActivity())

        layout_profile?.findViewWithTag<View>("tab_layout_profile_history")
            ?.fillProfileHistory(requireActivity())

        hasInit = true

        //endregion settings -> set button widths
    }
    //endregion INITIALIZE ALL TAB DATA

    //region on back pressed
    fun handleBackPress(callback: () -> Unit) = layout_image_user_profile.moveUserIconBack(callback)
    //endregion on back pressed
}
