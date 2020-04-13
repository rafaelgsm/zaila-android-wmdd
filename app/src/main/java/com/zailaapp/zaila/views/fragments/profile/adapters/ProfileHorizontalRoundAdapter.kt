package com.zailaapp.zaila.views.fragments.profile.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.accelerateInterpolation
import com.zailaapp.zaila.extensions.decelerateInterpolation
import kotlinx.android.synthetic.main.row_profile_indicator.view.*
import kotlin.math.absoluteValue

class ProfileHorizontalRoundAdapter(
    private val list: List<String>,
    private val recyclerView: RecyclerView,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<ProfileHorizontalRoundAdapter.VH>() {

    private var longestListItem: String

    init {
        longestListItem = list[0]

        list.forEach {
            if (longestListItem.length < it.length) {
                longestListItem = it
            }
        }
    }

    init {
        setupScrollManager()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_profile_indicator,
                parent, false
            )

        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position % list.size], longestListItem, clickListener)
    }

    override fun getItemCount(): Int {
        //Return "infinite", or regular size
        return Integer.MAX_VALUE
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(menuItem: String, longestTextItem: String, listener: (String) -> Unit) {

            itemView.tv_tab_title.text = menuItem

            //All items have the same size, by having the longest string to be invisible.
            itemView.tv_tab_title_.text = longestTextItem

            itemView.setOnClickListener { listener(menuItem) }
        }
    }


    //region scroll listener
    private fun setupScrollManager() {

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                //region control item alpha & roundness
                //Iterate over all visible views, and check their position:
                setupItemPositionState()
                //endregion control item alpha & roundness
            }
        })
    }
    //endregion scroll listener

    //region item position state setup

    private fun setupItemPositionState() {

        // GUARD STATEMENT HERE //
        val llm = recyclerView.layoutManager as? LinearLayoutManager ?: return
        // GUARD STATEMENT HERE //

        val screenWidth = recyclerView.width

        //Iterate over all visible views, and check their position:
        for (i in 0..100) {

            val firstVisibleItem =
                llm.findViewByPosition(llm.findFirstVisibleItemPosition() + i)

            if (firstVisibleItem == null) break

            val row_profile_indicator =
                firstVisibleItem.findViewById<View>(R.id.row_profile_indicator)


            //region proportion calculation
            val itemAbsolutePositionCenter =
                (firstVisibleItem.left + row_profile_indicator.width / 2f)

            val distanceFromCenter =
                (screenWidth) - itemAbsolutePositionCenter

            //0 -> CENTER
            //-0.5 -> MAX-LEFT
            //+0.5 -> MAX-RIGHT
            val proportion = 0.5f - distanceFromCenter / screenWidth

            //0 -> center
            //1 -> MAX-LEFT OR MAX-RIGHT
            val proportionPercent = (proportion / 0.5).absoluteValue

            val proportionPercentAccelerated = proportionPercent.toFloat().accelerateInterpolation()
            val proportionPercentDecelerated =
                proportionPercent.decelerateInterpolation(0.5).toFloat()

            row_profile_indicator.tv_test.text = "" + proportionPercent.toString().take(4)
            row_profile_indicator.tv_test2.text = "" + proportionPercent.toString().take(4)
            //endregion proportion calculation

            //region alpha
            row_profile_indicator.alpha = 1.1f - proportionPercentDecelerated
            //endregion alpha

            //region vertical position
            val maxPadding =
                row_profile_indicator.resources.getDimensionPixelSize(R.dimen.margin_bottom_menu_profile)
            //50.dp
            row_profile_indicator.setPadding(
                0,
                maxPadding - (maxPadding * proportionPercentAccelerated).toInt(),
                0,
                0
            )
            //endregion vertical position


            //region text resize
            val originalTextSize =
                row_profile_indicator.context.resources.getDimension(R.dimen.tv_tab_title_size)

            if (proportionPercentDecelerated < 0.3) {
                row_profile_indicator.tv_tab_title.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    originalTextSize
                )
            } else {

                var minTextSizeProportion = (proportionPercentDecelerated - 0.3f).toFloat()

                if (minTextSizeProportion > 0.3f) {
                    minTextSizeProportion = 0.3f
                }

                row_profile_indicator.tv_tab_title.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    originalTextSize - originalTextSize * (minTextSizeProportion)
                )
            }

            //endregion text resize


        }
    }
    //endregion item position state setup
}