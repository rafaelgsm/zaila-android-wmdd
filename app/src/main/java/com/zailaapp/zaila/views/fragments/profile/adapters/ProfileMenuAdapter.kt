package com.zailaapp.zaila.views.fragments.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication

/**
 *
 * This adapter is responsible to display all pages at the same time (side by side)
 *
 *
 * (Each page has its own adapter for their lists)
 * (The Activity passes the data using this adapter's methods)
 *
 * TIP:
 * Use TAGS to fill the layout, at the fragment/activity:
 * layout_profile?.findViewWithTag<View>("tab_layout_profile_settings")?.fillDataAtThisView(DATA)
 */
class ProfileMenuAdapter(private val mContext: Context) : PagerAdapter() {

    companion object {
        val tabs =
            arrayListOf(*ZailaApplication.instance.resources.getStringArray(R.array.profile_tabs))
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(mContext)

        var layout: ViewGroup? = null

        val layoutRes: Int = when (position) {
            0 -> R.layout.tab_layout_profile_settings
            1 -> R.layout.tab_layout_profile_zaila
            2 -> R.layout.tab_layout_profile_badges
            3 -> R.layout.tab_layout_profile_history

            else -> R.layout.tab_layout_profile_settings
        }

        layout = inflater.inflate(layoutRes, collection, false) as ViewGroup

        collection.addView(layout)
        return layout
    }


    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return tabs.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}