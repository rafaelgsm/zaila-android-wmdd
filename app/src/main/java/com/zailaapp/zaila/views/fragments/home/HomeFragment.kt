package com.zailaapp.zaila.views.fragments.home

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.*
import com.zailaapp.zaila.extensions.projectOnly.measureMuseumRow
import com.zailaapp.zaila.model.responses.Museum
import com.zailaapp.zaila.services.LocationGPSReceiver
import com.zailaapp.zaila.services.LocationManager
import com.zailaapp.zaila.utils.JsonProvider
import com.zailaapp.zaila.views.base.AdapterResizable
import com.zailaapp.zaila.views.base.BaseListResizableFragment
import com.zailaapp.zaila.views.fragments.home.adapters.MuseumAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_progress.*


class HomeFragment : BaseListResizableFragment(R.layout.fragment_home) {

    private val TAG = "HomeFragment"

    private lateinit var _viewModel: MuseumViewModel

    //Those fields can either be the remote or the mock classes:
//    private val _placesApi: PlacesApi by inject()
    // >> di/networkModule.kt

    private var _places: List<Museum>? = null

    private val _navController by lazy { findNavController() }

    //region BaseListResizableFragment contract
    override fun getRecyclerView(): RecyclerView? {
        return recyclerView_museum
    }

    override fun getLinearLayoutManager(): LinearLayoutManager? {
        return recyclerView_museum?.layoutManager as? LinearLayoutManager
    }

    override fun getAdapterResizable(): AdapterResizable? {
        return recyclerView_museum?.adapter as? MuseumAdapter
    }

    override fun getPushRateMenuOpen(): Double {
        return 1.1
    }

    override fun getPushRateMenuClosed(): Double {
        return 1.1
    }

    override fun getInterpolatorFactor(): Double {
        return 1.0
    }
    //endregion BaseListResizableFragment contract

    //region initialize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewModel = ViewModelProvider(this).get()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        requireView().findViewById<View>(R.id.btn_details)

        setupListeners()
        setObservers()

//        context?.let {
//            if (!it.hasNfc()) {
//                tv_nfc_info.text = resources.getString(R.string.error_nfc)
//                tv_nfc_info.setTextColor(ContextCompat.getColor(it, R.color.red))
//            }
//        }

    }
    //endregion initialize

    //region setupListeners
    private fun setupListeners() {

        edt_search_museum_image?.afterMeasured {
            edt_search_museum_image?.setWidthHeight(height, height)
        }
        edt_search_museum_image?.forceLayout()

        edt_search_museum?.afterTextChangedDelay {
            (recyclerView_museum?.adapter as? MuseumAdapter)?.filter(it)

            //Doing this scroll to force all items to adapt:
            recyclerView_museum?.smoothScrollBy(0, 1)
        }

        edt_search_museum?.onSelectChangeIconColor(
            R.color.colorPrimary,
            R.color.colorAccent,
            edt_search_museum_image
        )
        edt_search_museum?.onDone {
            view_focus_placeholder?.requestFocus()
            edt_search_museum?.closeKeyboard()
        }
    }
    //endregion setupListeners

    //region observers
    /**
     * Sets the observers to update the UI as soon as it changes on the ViewModel:
     */
    private fun setObservers() {

        _viewModel.isLoading.observe(viewLifecycleOwner) {
            progress?.visibility = if (it) View.VISIBLE else View.GONE
        }

        _viewModel.errorMessage.observe(viewLifecycleOwner) {
            context?.toast(it)
        }


        _viewModel.museumList.observe(viewLifecycleOwner) {

            if (_places == null) {

                _places = it

                activity?.runOnUiThread(::setupMuseumList)
            }

        }

    }

    //endregion observers

    //region setup lists

    /**
     * Sets up the museum list if the layout is available and we have the places object loaded.
     */
    private fun setupMuseumList() {

        if (_places == null) return


        /**
         * The item height will be measured based on the space between the top of
         * the list, and the expanded USER MENU:
         */

        //Minus recyclerView top padding...z
        val listHeight =
            recyclerView_museum.measuredHeight - resources.getDimensionPixelSize(R.dimen.padding_top_row_museum)
        val userMenuHeight = getMenuHeight()

        val availableSpace = listHeight - userMenuHeight

        //1. If the final inflated list item is bigger than the available space / 3...
        var itemHeight = availableSpace / 3

        activity?.measureMuseumRow { minimumHeightRequired ->

            if (itemHeight < minimumHeightRequired) {
                itemHeight = minimumHeightRequired
            }


            //2. otherwise make the list item occupy a little less than a third:
            if (itemHeight * 3 > availableSpace) {
                itemHeight = (availableSpace / 2.0).toInt()

                ZailaApplication.isShortPhoneHeight = true
            }

            //Setup the adapter with the list items having a specific height:
            setupAdapterForMenus(itemHeight)

        }
        //...


        setupAdapterForMenus(itemHeight)

    }

    private fun setupAdapterForMenus(itemHeight: Int) {
        val adapter = MuseumAdapter(
            _places!!,
            itemHeight
        )
        {

            val directions = HomeFragmentDirections.actionGoMuseum(JsonProvider.toJson(it))
            _navController.navigateOnce(directions)

        }

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView_museum?.layoutManager = llm

        recyclerView_museum?.adapter = adapter
        recyclerView_museum?.setBouncyDecor()

        setupScrollAnimation()  //BaseListResizableFragment
    }
    //endregion setup lists

    //region load nearby museums
    private fun loadNearbyMuseums(location: Location?) {

        if (location != null) {

            Log.d(TAG, "${location.latitude}, ${location.longitude}")

            val city = showAddressGetCity(location)

            //todo - ...
            _viewModel.getMuseumList(city)

            Log.d(TAG, "city: ${city}")

        } else {
            Log.d(TAG, "location == null")
        }

    }

    //endregion load nearby museums

    //region get current address from location
    private fun showAddressGetCity(location: Location): String { // Get the location manager

        val geo = Geocoder(context!!)

        var addresses: List<Address>

        try {
            addresses = geo.getFromLocation(location.latitude, location.longitude, 1)
        } catch (e: Exception) {
            addresses = listOf()
        }


        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressText: String = addresses[0].getAddressLine(0)

            tv_current_location?.text = addressText

            return address.locality
        } else {
            tv_current_location.text = "No address found."
        }

        return ""

    }
    //endregion get current address from location

    //region permission check location
    //todo - check if museums were loaded already
    private var hasLoadedMuseums = false

    override fun onResume() {
        super.onResume()

        checkPermissionsAndRunLocationService()
        //.......

    }

    override fun onPause() {
        super.onPause()

        LocationGPSReceiver.listener = {}

    }

    override fun onDestroy() {
        super.onDestroy()

        LocationManager.stop()
    }

    private fun checkPermissionsAndRunLocationService() {
        if (hasPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {

            loadMuseumsCheck()

        } else {

            //........
            requestPermissionsBase(
                {
                    if (it) {

                        //granted!
                        loadMuseumsCheck()

                    } else {

                        //todo-- dialog warning the need of the camera?
                        //do nothing? show message?

                    }
                },
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
//endregion permission check location


    //todo - check if museums were loaded already
    private fun loadMuseumsCheck() {
        if (!hasLoadedMuseums) {

            Log.d(TAG, "LocationManager.start")
            LocationManager.start {
                loadNearbyMuseums(it)
            }

            LocationGPSReceiver.listener = {
                activity?.runOnUiThread { onGPSChanged() }
            }

            onGPSChanged()

            hasLoadedMuseums = true
        }
    }

    //GPS CHECK TRIGGERS THE GPS RECEIVER!!!
    private var isFromSelfGpsCheck = false

    private fun onGPSChanged() {

        Log.d(TAG, "isFromSelfGpsCheck = ${isFromSelfGpsCheck}")

        if (isFromSelfGpsCheck) {
            isFromSelfGpsCheck = false
            return
        }

        if (context == null) return

        isFromSelfGpsCheck = true
        if (context!!.isGpsEnabled()) {

            Log.d(TAG, "GPS ON")

            if (_viewModel.museumList.value == null || _viewModel.museumList.value!!.isEmpty()) {

                Log.d(TAG, "checkPermissionsAndRunLocationService...")
                hasLoadedMuseums = false
                checkPermissionsAndRunLocationService()
            }

        } else {
            //todo - gps not active!
            Log.d(TAG, "GPS OFF")
        }
    }

//Navigate:
//        navController.navigate(R.id.actionGoFragment2)
//        navController.navigateUp()
//        navController.popBackStack()

//SafeArgs:
//        val directions = Fragment1Directions.actionGoFragment2("some text")
//        navController.navigate(directions)

}