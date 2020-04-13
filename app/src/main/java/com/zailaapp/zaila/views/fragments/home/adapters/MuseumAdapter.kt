package com.zailaapp.zaila.views.fragments.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.*
import com.zailaapp.zaila.extensions.projectOnly.fillMuseum
import com.zailaapp.zaila.extensions.projectOnly.forceMeasure
import com.zailaapp.zaila.model.responses.Museum
import com.zailaapp.zaila.views.base.AdapterResizable
import kotlinx.android.synthetic.main.row_museum.view.*

/**
 * Items are adjusted on the fly, as the user scrolls.
 *
 * We are using the item isOpen attribute PLUS the item view itself (ie.: checking view's alpha)
 * to determine the item state.
 */
class MuseumAdapter(
    private var list: List<Museum>,
    private var itemHeight: Int,
    private val clickListener: (Museum) -> Unit
) : RecyclerView.Adapter<MuseumAdapter.VH>(), AdapterResizable {

    //Value indicating the HEIGHT of the "header" of each item. (The circle image in this case is being pushed below this header as the item shrinks)
    private var percentageTopHeight: Float = 0f

    companion object {

        private var percentageTopHeight_MULT = 0.85f

        private var GUIDELINE_IMAGE_START_0 = 0f
        private var GUIDELINE_IMAGE_START_1 = 0.35f

        private var WIDTH_PERCENT_0 = 0.25f
        private var WIDTH_PERCENT_1 = 0.3f

        private fun adjust(itemView: View, item: Museum, percentageTopHeight: Float) {

            if (item.isOpen) {

                if (percentageTopHeight > 0) {
                    itemView.guideline_museum_row_top_image.moveGuidelineTo(percentageTopHeight * percentageTopHeight_MULT)
                    itemView.guideline_museum_row_image_start.moveGuidelineTo(
                        GUIDELINE_IMAGE_START_0
                    )

                    itemView.img_museum_background.changeWidthPercent(WIDTH_PERCENT_0)
                }

                itemView.img_category_museum_background.horizontalBias(0f)

                itemView.layout_title_row_museum_background_plain.alpha = 1f
                itemView.layout_bottom_museum.alpha = 1f
                itemView.bg_layout_bottom_museum.alpha = 1f
            } else {


                itemView.guideline_museum_row_top_image.moveGuidelineTo(percentageTopHeight)

                itemView.guideline_museum_row_image_start.moveGuidelineTo(
                    GUIDELINE_IMAGE_START_1
                )

                itemView.img_museum_background.changeWidthPercent(WIDTH_PERCENT_1)

                itemView.layout_title_row_museum_background_plain.alpha = 0f
                itemView.layout_bottom_museum.alpha = 0f
                itemView.bg_layout_bottom_museum.alpha = 0f
            }
        }
    }


    private var extraItems: Int = 2

    private val _originalList: ArrayList<Museum> = arrayListOf()
    private var _filter = ""

    init {

        //After third item, make them all closed, so the layout will initialize correctly.
        list = list.mapIndexed { index, museum ->
            if (index >= 3) {
                museum.isOpen = false
            }
            museum
        }

        _originalList.addAll(list)
    }

    //region filter
    fun filter(filter: String) {

        _filter = filter

        if (_filter.isNotEmpty()) {

            list = _originalList.filter { it.name!!.contains(_filter, true) && it.museumId!! >= 0 }

        } else {
            list = _originalList
        }

        notifyDataSetChanged()

    }
    //endregion filter

    /**
     * Using viewtype to identify each item and assign a tag as an id.
     * We are doing this in order to map which item is open/closed.
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_museum,
                parent, false
            )

        val p = v.layoutParams
        p.height = itemHeight
        v.layoutParams = p

        v.row_item_resizable.tag = "${viewType}"

        if (percentageTopHeight == 0f) {
            v.layout_title_row_museum.forceMeasure()
            percentageTopHeight =
                v.layout_title_row_museum.measuredHeight / itemHeight.toFloat()
        }

        v.guideline_museum_row_top_image.moveGuidelineTo(percentageTopHeight * percentageTopHeight_MULT)

        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        if (position > list.lastIndex) {
            holder.bind(Museum(-1), percentageTopHeight, clickListener)
        } else {
            holder.bind(list[position], percentageTopHeight, clickListener)
        }
    }

    override fun getItemCount(): Int {
        //Return "infinite", or regular size
//        return if (loopThrough) Integer.MAX_VALUE else list?.size ?: 0


        //todo Adding more invisible items wo the "last" item can actually display completely
        return list.size + extraItems
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Museum, percentageTopHeight: Float, listener: (Museum) -> Unit) {

            //region layout state control
            adjust(itemView, item, percentageTopHeight)
            //endregion layout state control

            if (item.museumId != null && item.museumId < 0) {
                itemView.row_item_resizable.invis()
            } else {
                itemView.row_item_resizable.vis()
                itemView.row_item_resizable.fillMuseum(item)

                itemView.row_item_resizable.setOnClickListener {
                    listener(item)
                }
            }

            //Using these 2 to validate the spacing:
            itemView.tv_calc_1?.postDelayed({
                try {
                    itemView.tv_calc_1?.text = itemView.tv_calc_1?.width.toString()
                } catch (e: Exception) {
                }

            }, 300)

            itemView.tv_calc_2?.postDelayed({
                try {
                    itemView.tv_calc_2?.text = itemView.tv_calc_2?.width.toString()
                } catch (e: Exception) {
                }

            }, 300)


        }
    }

    //region adjustItem
    //todo - refactor - Fragment is iterating though all items, with the layoutmanager...
    override fun adjustItem(proportion: Double, currentWidth: Int, item: View) {


        if (proportion >= 0) {

            val min = (percentageTopHeight * percentageTopHeight_MULT)

            item.guideline_museum_row_top_image.moveGuidelineTo(
                proportion.getValueBasedOnProportion(
                    min,
                    percentageTopHeight
                )
            )

            item.guideline_museum_row_image_start.moveGuidelineTo(
                proportion.getValueBasedOnProportion(
                    GUIDELINE_IMAGE_START_0,
                    GUIDELINE_IMAGE_START_1
                )
            )

            item.img_museum_background.changeWidthPercent(
                proportion.getValueBasedOnProportion(
                    WIDTH_PERCENT_0,
                    WIDTH_PERCENT_1
                )
            )

            //We move the guideline that holds the image, as the proportion changes:
            item.img_category_museum_background.horizontalBias(proportion.toFloat() / 2)

            val position = item.row_item_resizable.tag.toString().toInt()

            if (proportion > 0.35 && item.layout_bottom_museum.alpha == 1f && position <= list.lastIndex) {

                //If its past this threshold and item is fully alpha, then fade out:
                list[position].isOpen = false

                item.layout_title_row_museum_background_plain.animateFadeOutTagAnim(200L)
                item.layout_bottom_museum.animateFadeOutTagAnim(200L)
                item.bg_layout_bottom_museum.animateFadeOutTagAnim(200L)

            } else {

                //Scrolling from bottom to top, then will fade in back the text after this threshold:
                if (proportion <= 0.35) {
                    fadeInItems(item)
                }

            }

        } else {

            //Will fall here if the item is before threshold (fully stretched)

            val position = item.row_item_resizable.tag.toString().toInt()
            //Only try to change back to default, if it was not yet changed.
            //(Using alpha to check the state)
            if (item.layout_bottom_museum.alpha < 1f && position <= list.lastIndex) {

                list[item.row_item_resizable.tag.toString().toInt()].isOpen = true

                item.guideline_museum_row_top_image.moveGuidelineTo(percentageTopHeight * percentageTopHeight_MULT)
                item.guideline_museum_row_image_start.moveGuidelineTo(GUIDELINE_IMAGE_START_0)
                item.img_museum_background.changeWidthPercent(WIDTH_PERCENT_0)

                item.img_category_museum_background.horizontalBias(0f)

//                item.tv_name_museum.setTextSize(
//                    TypedValue.COMPLEX_UNIT_PX,
//                    item.context.resources.getDimension(R.dimen.tv_name_museum)
//                )

                fadeInItems(item)

            }


        }//end else proportion >= 0 check

    }

    //endregion adjustItem

    private fun fadeInItems(item: View) {
        item.layout_title_row_museum_background_plain.animateFadeInTagAnim(200L)
        item.layout_bottom_museum.animateFadeInTagAnim(200L)
        item.bg_layout_bottom_museum.animateFadeInTagAnim(200L)
    }
}