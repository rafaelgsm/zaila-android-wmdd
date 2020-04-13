package com.zailaapp.zaila.extensions.projectOnly

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.afterMeasuredHeight
import com.zailaapp.zaila.extensions.afterMeasuredWidth
import com.zailaapp.zaila.extensions.setHeight
import com.zailaapp.zaila.utils.PreferencesManager
import com.zailaapp.zaila.views.activities.LoginActivity
import kotlinx.android.synthetic.main.row_history.view.*
import kotlinx.android.synthetic.main.tab_layout_profile_badges.view.*

/**
 * Contact Zaila
 * Terms of use
 * Licenses
 * Logout
 */
fun View.fillProfileSettings(activity: Activity?) {

    val btn_contact = findViewById<View>(R.id.btn_contact)
    val btn_terms = findViewById<View>(R.id.btn_terms)
    val btn_licences = findViewById<View>(R.id.btn_licences)
    val btn_logout = findViewById<View>(R.id.btn_logout)

    //todo...

    btn_logout.setOnClickListener {
        PreferencesManager.logout()

        activity?.startActivity(LoginActivity.newIntent())
        activity?.finish()
    }
}

/**
 * Language
 *  ----
 *
 * Audio Description
 *  ----
 *
 * Quests
 *  ----
 */
fun View.fillProfileZaila(activity: Activity?) {

    val layout_container_language =
        findViewById<View>(R.id.layout_language)?.findViewById<View>(R.id.layout_container_item_profile)
    val layout_container_audio =
        findViewById<View>(R.id.layout_audio)?.findViewById<View>(R.id.layout_container_item_profile)
    val layout_container_quest =
        findViewById<View>(R.id.layout_quest)?.findViewById<View>(R.id.layout_container_item_profile)


    //Makes all containers have same height (Same height as the Languages container)
    layout_container_language?.afterMeasuredHeight {

        layout_container_audio?.setHeight(layout_container_language.height)
        layout_container_quest?.setHeight(layout_container_language.height)
    }
}

//region BADGES
/**
 * BADGES:
 */
fun View.fillProfileBadges(activity: Activity?) {

    if (activity == null) return

    recyclerView_badges.forceMeasure()

    val recyclerView_badges =
        findViewById<RecyclerView>(R.id.recyclerView_badges)

    val adapter = BadgesAdapter(
        arrayListOf(
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",

            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",

            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4"
        ),
        recyclerView_badges.measuredHeight,
        3f
    )
    {
        //Item click

    }

    val llm = GridLayoutManager(context, 4)
    recyclerView_badges.layoutManager = llm

    recyclerView_badges.adapter = adapter

}

//region BadgesAdapter
class BadgesAdapter(
    private val list: List<String>,
    private var maxWidth: Int = 0,
    private var COLUMNS_VISIBLE: Float,
    private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<BadgesAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_badge,
                parent, false
            )

        val p = v.layoutParams
        p.width = (maxWidth / COLUMNS_VISIBLE).toInt()
        p.height = (maxWidth / COLUMNS_VISIBLE).toInt()
        v.layoutParams = p

        return VH(v)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list.get(position), clickListener)
    }


    override fun getItemCount(): Int {
        //Return "infinite", or regular size
//        return if (loopThrough) Integer.MAX_VALUE else list?.size ?: 0
        return list.size
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(s: String, listener: (Int) -> Unit) {
            itemView.setOnClickListener {
                listener(adapterPosition)
            }
        }
    }
}
//endregion BadgesAdapter

//endregion BADGES


//region History
fun View.fillProfileHistory(activity: Activity?) {

//    val recyclerView_history =
//        findViewById<RecyclerView>(R.id.recyclerView_history)
//
//    val adapter = HistoryAdapter(arrayListOf("Item 1", "Item 2", "Item 3"))
//    {
//        //Item click
//        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//    }
//
//    val llm = LinearLayoutManager(context)
//    llm.orientation = LinearLayoutManager.VERTICAL
//    recyclerView_history.layoutManager = llm
//
//    recyclerView_history.adapter = adapter
}

//region adater History
class HistoryAdapter(
    private val list: List<String>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_history,
                parent, false
            )

        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list.get(position), clickListener)
    }

    override fun getItemCount(): Int {
        //Return "infinite", or regular size
//        return if (loopThrough) Integer.MAX_VALUE else list?.size ?: 0
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String, listener: (String) -> Unit) {

            itemView.img_history.afterMeasuredWidth {

            }

//            itemView.row_menu_item.setOnClickAlphaAnimation {
//                listener(menuItem)
//            }

        }
    }
}
//endregion adapter History
//endregion History