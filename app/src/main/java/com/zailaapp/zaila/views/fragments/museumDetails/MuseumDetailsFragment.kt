package com.zailaapp.zaila.views.fragments.museumDetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.afterMeasured
import com.zailaapp.zaila.extensions.navigateOnce
import com.zailaapp.zaila.extensions.projectOnly.fillMuseum
import com.zailaapp.zaila.extensions.projectOnly.measureTopExhibitionRow
import com.zailaapp.zaila.extensions.setBouncyDecor
import com.zailaapp.zaila.extensions.toast
import com.zailaapp.zaila.model.responses.Exhibition
import com.zailaapp.zaila.model.responses.Museum
import com.zailaapp.zaila.utils.JsonProvider
import com.zailaapp.zaila.views.base.AdapterResizable
import com.zailaapp.zaila.views.base.BaseListResizableFragment
import com.zailaapp.zaila.views.fragments.museumDetails.adapter.ExhibitionAdapter
import kotlinx.android.synthetic.main.fragment_museum_details.*
import kotlinx.android.synthetic.main.layout_progress.view.*

class MuseumDetailsFragment : BaseListResizableFragment(R.layout.fragment_museum_details) {

    private lateinit var _viewModel: MuseumDetailsViewModel
    private var _exhibitionList: List<Exhibition>? = null

    private val _navController by lazy { findNavController() }

    private val args by navArgs<MuseumDetailsFragmentArgs>()

    //region BaseListResizableFragment contract
    override fun getRecyclerView(): RecyclerView? {
        return recyclerView_exhibitions
    }

    override fun getLinearLayoutManager(): LinearLayoutManager? {
        return recyclerView_exhibitions?.layoutManager as? LinearLayoutManager
    }

    override fun getAdapterResizable(): AdapterResizable? {
        return recyclerView_exhibitions?.adapter as? ExhibitionAdapter
    }

    override fun getPushRateMenuOpen(): Double {
        return 2.0
    }

    override fun getPushRateMenuClosed(): Double {
        return 2.0
    }

    override fun getInterpolatorFactor(): Double {
        return 1.3
    }
    //endregion BaseListResizableFragment contract

    //region initialize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewModel = ViewModelProvider(this).get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val museum: Museum = JsonProvider.fromJson(args.museum)
        toolbar_museum.fillMuseum(museum, true)
        img_museum.fillMuseum(museum, true)
        layout_museum_schedule_container.fillMuseum(museum, true)

        setObservers()

        recyclerView_exhibitions.afterMeasured {
            _viewModel.getMuseum(museum.museumId!!)
        }
    }

    private fun setObservers() {
        _viewModel.isLoading.observe(viewLifecycleOwner) {

            progress_museum_hours.progress_bar?.visibility = if (it) View.VISIBLE else View.GONE

            progress_exhibition_list.progress?.visibility = if (it) View.VISIBLE else View.GONE

        }

        _viewModel.errorMessage.observe(viewLifecycleOwner) {
            context?.toast(it)
        }


        _viewModel.museum.observe(viewLifecycleOwner) {

            toolbar_museum.fillMuseum(it, true)
            img_museum.fillMuseum(it, true)
            layout_museum_schedule_container.fillMuseum(it, true)

            if (_exhibitionList == null) {

                _exhibitionList = it.exhibitionsList


                activity?.measureTopExhibitionRow { heightHeader, heightTop ->

                    activity?.runOnUiThread {
                        setupExhibitionList(heightHeader, heightTop)
                    }
                }


            }

        }
    }

    //region setup lists

    /**
     * Sets up the museum list if the layout is available and we have the places object loaded.
     */
    private fun setupExhibitionList(heightHeader: Int, heightTop: Int) {

        /**
         * The item height will be measured based on the space between the top of
         * the list, and the expanded USER MENU:
         */

        //Minus recyclerView top padding...
        val listHeight =
            recyclerView_exhibitions.measuredHeight - resources.getDimensionPixelSize(R.dimen.padding_top_row_museum)
        val userMenuHeight = getMenuHeight()

        val itemHeight =
            if (ZailaApplication.isShortPhoneHeight) {
                (listHeight - userMenuHeight) / 1f
            } else {
                (listHeight - userMenuHeight) / 2f
            }.toInt()


        val adapter = ExhibitionAdapter(
            _exhibitionList!!,
            itemHeight,
            heightHeader / itemHeight.toFloat(),
            heightTop / itemHeight.toFloat()
        )
        {
            val directions =
                MuseumDetailsFragmentDirections.actionGoExhibition(JsonProvider.toJson(it))
            _navController.navigateOnce(directions)
        }

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView_exhibitions.layoutManager = llm

        recyclerView_exhibitions.adapter = adapter
        recyclerView_exhibitions.setBouncyDecor()

        setupScrollAnimation()  //BaseListResizableFragment
    }
    //endregion setup lists
}
