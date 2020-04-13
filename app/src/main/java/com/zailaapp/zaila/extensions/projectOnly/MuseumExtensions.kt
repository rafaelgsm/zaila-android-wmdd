package com.zailaapp.zaila.extensions.projectOnly

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.loadRound
import com.zailaapp.zaila.extensions.setBitmapFromVector
import com.zailaapp.zaila.model.responses.Museum
import java.util.*

fun View.fillMuseum(museum: Museum, detail: Boolean = false) {

    val img_museum = findViewById<ImageView>(R.id.img_museum)

    val img_category_museum = findViewById<ImageView>(R.id.img_category_museum)

    val tv_name_museum = findViewById<TextView>(R.id.tv_name_museum)
    val tv_address_museum = findViewById<TextView>(R.id.tv_address_museum)

//    val tv_desc_museum = findViewById<TextView>(R.id.tv_desc_museum)
    val tv_featuring = findViewById<TextView>(R.id.tv_featuring)
    val tv_featuring_more = findViewById<TextView>(R.id.tv_featuring_more)

    //Museum image:

    if (detail) {
        museum.imageURL?.let {
            img_museum?.loadRound(it, 100)
        }
    } else {
        museum.imageURL?.let {
            img_museum?.loadRound(it)
        }
    }


    fun getCategoryIcon(categoryId: Int?): Int {
        return when (categoryId) {
            1 -> R.drawable.ic_category_science
            2 -> R.drawable.ic_category_art
            3 -> R.drawable.ic_category_anthropology
            4 -> R.drawable.ic_category_history
            else ->
                R.drawable.ic_category_science
        }
    }

    //Category image:
    img_category_museum?.setBitmapFromVector(getCategoryIcon(museum.museum_category?.categoryId))


    tv_name_museum?.text = museum.name
    tv_address_museum?.text = museum.address
//    tv_desc_museum?.text = museum.description


    //Featuring "Cindy Sherman and 3 more Exhibitions":
    if (museum.exhibitionsList != null) {

        var featuringText = ""

        if (museum.exhibitionsList.isNotEmpty()) {
            featuringText = "${museum.exhibitionsList[0].name}"
        }

        tv_featuring?.text = featuringText

        if (museum.exhibitionsList.size > 1) {
            tv_featuring_more?.text =
                resources.getString(R.string.and) + " ${(museum.exhibitionsList.size - 1)} " + resources.getString(
                    R.string.others
                )
        }
    }

    if (!detail) {
        tv_name_museum?.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.tv_name_museum)
        )
        tv_address_museum?.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.tv_address_museum)
        )
        tv_featuring?.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.tv_featuring_label)
        )
        tv_featuring_more?.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.tv_address_museum)
        )

        if (ZailaApplication.isShortPhoneHeight) {

            tv_featuring_more?.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.tv_featuring_label_small)
            )

//            tv_featuring_more?.invis()
        }
    }

    fillMuseumOpeningHours(museum)
}

fun View.fillMuseumOpeningHours(museum: Museum) {

    val layout_museum_schedule = findViewById<LinearLayout>(R.id.layout_museum_schedule)

    val weekDayString: String = getWeekString()

    layout_museum_schedule?.let { containerHours ->

        museum.openingHours?.let { openingHours ->
            openingHours.forEachIndexed { index, openingHours ->

                if (index < containerHours.size) {
                    val textView = containerHours.getChildAt(index)

                    if (textView is TextView) {

                        var dayRow =
                            openingHours.day!!.removeRange(
                                3,
                                openingHours.day.length
                            ) + ". " + openingHours.startTime!!.cutSeconds() + " - " + openingHours.endTime!!.cutSeconds()

                        //Sets first letter to uppercase:
                        dayRow = dayRow.replaceFirst(dayRow[0], dayRow[0].toUpperCase())

                        textView.text = dayRow


                        if (weekDayString.toLowerCase() == openingHours.day?.toLowerCase()) {
                            textView.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_bold)
                        }
                    }
                }
            }
        }

    }//containerHours .let

}

private fun String.cutSeconds(): String {
    return this.replaceRange(IntRange(this.length - 3, this.length - 1), "")
}

private fun getWeekString(): String {

    val calendar: Calendar = Calendar.getInstance()
    val day: Int = calendar.get(Calendar.DAY_OF_WEEK)

    return when (day) {
        Calendar.SUNDAY -> "sunday"
        Calendar.MONDAY -> "monday"
        Calendar.TUESDAY -> "tuesday"
        Calendar.WEDNESDAY -> "wednesday"
        Calendar.THURSDAY -> "thursday"
        Calendar.FRIDAY -> "friday"
        Calendar.SATURDAY -> "saturday"
        else -> ""
    }
}