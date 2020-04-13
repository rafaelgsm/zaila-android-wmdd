package com.zailaapp.zaila.views.fragments.museumDetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.*
import com.zailaapp.zaila.extensions.projectOnly.fillExhibition
import com.zailaapp.zaila.model.responses.Exhibition
import com.zailaapp.zaila.views.base.AdapterResizable
import kotlinx.android.synthetic.main.row_exhibition.view.*

/**
 * Items are adjusted on the fly, as the user scrolls.
 *
 * We are using the item isOpen attribute PLUS the item view itself (ie.: checking view's alpha)
 * to determine the item state.
 */
class ExhibitionAdapter(
    private var list: List<Exhibition>,
    private var itemHeight: Int,

    //Horizontal guideline will move UP and down based on those 2 values:
    private val heightHeaderPercent: Float,  //% from the top of the item to the bottom of the header
    private val heightCenterPercent: Float,  //% from the top of the item to the center of the IMG view


    private val clickListener: (Exhibition) -> Unit
) : RecyclerView.Adapter<ExhibitionAdapter.VH>(), AdapterResizable {

    private val _originalList: ArrayList<Exhibition> = arrayListOf()

    init {
        _originalList.addAll(list)
    }

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
                R.layout.row_exhibition,
                parent, false
            )

        val p = v.layoutParams
        p.height = itemHeight
        v.layoutParams = p

        v.row_item_resizable.tag = "${viewType}"

        //Change the guideline and move it to the position %:
        v.tv_title_exhibition_bg.changeConstraintBotBotOf(v.guideline_tv_title_exhibition_bg)
        v.guideline_tv_title_exhibition_bg.moveGuidelineTo(heightCenterPercent)


        v.view_padding_guideline_start.changeConstraintEndEndOfParent()


        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        if (position > list.lastIndex) {
            holder.bind(Exhibition(-1), clickListener)
        } else {
            holder.bind(list[position], clickListener)
        }
    }

    override fun getItemCount(): Int {
        //todo Adding more invisible items to the "last" item can actually display completely
        return list.size + 2
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Exhibition, listener: (Exhibition) -> Unit) {

            //region layout state control
            adjust(itemView, heightHeaderPercent, heightCenterPercent, item)
            //endregion layout state control

            if (item.museumId != null && item.museumId < 0) {
                itemView.row_item_resizable.invis()
            } else {
                itemView.row_item_resizable.vis()
                itemView.row_item_resizable.fillExhibition(item)

                itemView.row_item_resizable.setOnClickListener {
                    listener(item)
                }
            }

        }
    }

    /**
     * This one is called every time the user scrolls.
     * It adjusts every item in the list.
     *
     * So to reduce the amount of operations we are adding some validation to only change the items when necessary.
     */
    override fun adjustItem(proportion: Double, currentWidth: Int, item: View) {


        if (proportion >= 0) {

            item.guideline_tv_title_exhibition_bg.moveGuidelineTo(
                proportion.getValueBasedOnProportion(
                    heightCenterPercent,
                    heightHeaderPercent
                )
            )

            item.guideline_start.moveGuidelineTo(
                proportion.getValueBasedOnProportion(
                    0f,
                    0.15f
                )
            )

            item.guideline_end.moveGuidelineTo(
                proportion.getValueBasedOnProportion(
                    0.85f,
                    1f
                )
            )

            item.tv_title_exhibition.horizontalBias(
                proportion.getValueBasedOnProportion(
                    0f,
                    0.5f
                )
            )

            item.tv_title_exhibition_padding_start.horizontalBias(
                proportion.getValueBasedOnProportion(
                    0f,
                    0.5f
                )
            )


            item.img_exhibition.horizontalBias(proportion.toFloat() / 2)
            item.img_category_exhibition.horizontalBias(proportion.toFloat() / 2)
            //...


            val position = item.row_item_resizable.tag.toString().toInt()

            //region ALPHA VALIDATION
            if (proportion > 0.35 && item.layout_body_exhibition_white.alpha == 1f && position <= list.lastIndex) {

                //If its past this threshold and item is fully alpha, then fade out:
                list[position].isOpen = false

                item.layout_body_exhibition_white.animateFadeOutTagAnim(200L)
                item.layout_body_exhibition.animateFadeOutTagAnim(200L)
                item.layout_body_exhibition_golden_top_eraser_stroke.invis()


            } else {

                if (position <= list.lastIndex) {
                    list[position].isOpen = true
                }


                //Scrolling from bottom to top, then will fade in back the text after this threshold:
                if (proportion <= 0.35) {
                    fadeInItems(item)
                }

            }

            //endregion ALPHA VALIDATION

        } else {

            //Will fall here if the item is before threshold (fully stretched)
            //(Using alpha to check the state)
            val position = item.row_item_resizable.tag.toString().toInt()
            if (item.layout_body_exhibition_white.alpha < 1f && position <= list.lastIndex) {

                //todo - move this to the alpha validation later:
                list[item.row_item_resizable.tag.toString().toInt()].isOpen = true

                item.guideline_tv_title_exhibition_bg.moveGuidelineTo(heightCenterPercent)

                item.guideline_end.moveGuidelineTo(0.85f)
                item.guideline_start.moveGuidelineTo(0f)

                item.tv_title_exhibition.horizontalBias(
                    0f
                )

                item.tv_title_exhibition_padding_start.horizontalBias(
                    0f
                )

                item.img_exhibition.horizontalBias(0f)
                item.img_category_exhibition.horizontalBias(0f)

                fadeInItems(item)
            }


        }//end else proportion >= 0 check
    }

    private fun fadeInItems(item: View) {

        item.layout_body_exhibition_white.animateFadeInTagAnim(200L)
        item.layout_body_exhibition.animateFadeInTagAnim(200L)

        item.layout_body_exhibition_golden_top_eraser_stroke.vis()


    }

    companion object {

        /**
         * This one is called when the adapter creates the item (When it shows from bottom or top)
         * So it only has 2 states: open and closed (stretched and shrank)
         */
        private fun adjust(
            itemView: View,
            heightHeaderPercent: Float,
            heightCenterPercent: Float,
            item: Exhibition
        ) {

            if (item.isOpen) {

                itemView.guideline_tv_title_exhibition_bg.moveGuidelineTo(heightCenterPercent)
                itemView.guideline_end.moveGuidelineTo(0.85f)
                itemView.guideline_start.moveGuidelineTo(0f)

                itemView.tv_title_exhibition.horizontalBias(
                    0f
                )

                itemView.tv_title_exhibition_padding_start.horizontalBias(
                    0f
                )

                itemView.img_exhibition.horizontalBias(0f)
                itemView.img_category_exhibition.horizontalBias(0f)


            } else {

                itemView.guideline_tv_title_exhibition_bg.moveGuidelineTo(heightHeaderPercent)
                itemView.guideline_end.moveGuidelineTo(1f)
                itemView.guideline_start.moveGuidelineTo(0.15f)

                itemView.tv_title_exhibition.horizontalBias(
                    1f
                )

                itemView.tv_title_exhibition_padding_start.horizontalBias(
                    0.5f
                )

                itemView.img_exhibition.horizontalBias(0.5f)
                itemView.img_category_exhibition.horizontalBias(0.5f)
            }
        }
    }

}