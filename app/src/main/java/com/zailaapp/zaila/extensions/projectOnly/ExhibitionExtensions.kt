package com.zailaapp.zaila.extensions.projectOnly

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.loadRound
import com.zailaapp.zaila.extensions.loadRoundFromVector
import com.zailaapp.zaila.model.responses.Exhibition

@SuppressLint("DefaultLocale")
fun View.fillExhibition(exhibition: Exhibition, details: Boolean = false) {

    val img_exhibition = findViewById<ImageView>(R.id.img_exhibition)
    val img_category_exhibition = findViewById<ImageView>(R.id.img_category_exhibition)

    val tv_title_exhibition = findViewById<TextView>(R.id.tv_title_exhibition)
    val tv_date_exhibition = findViewById<TextView>(R.id.tv_date_exhibition)
    val tv_desc_exhibition = findViewById<TextView>(R.id.tv_desc_exhibition)

    fun getCategoryIcon(categoryId: Int?): Int {
        return when (categoryId) {
            1 -> R.drawable.ic_category_arts
            2 -> R.drawable.ic_category_photography
            3 -> R.drawable.ic_category_nature
            4 -> R.drawable.ic_category_astrology
            else ->
                R.drawable.ic_category_arts
        }
    }

    //Category image:
    img_category_exhibition?.loadRoundFromVector(getCategoryIcon(exhibition.categoryId))

    //Museum image:
    if (details) {
        exhibition.imageURL?.let { img_exhibition?.loadRound(it, 100) }
    } else {

//        if (exhibition.imageURL == "") {
//            img_exhibition?.loadRoundFromVector(getCategoryIcon(-1))
//        } else {
        exhibition.imageURL?.let { img_exhibition?.loadRound(it) }
//        }

        if (ZailaApplication.isShortPhoneHeight) {
            tv_desc_exhibition?.setLines(1)
        }
    }

    tv_title_exhibition?.text = exhibition.name


    //JAN 01, 2019 - MAY 10, 2020
    tv_date_exhibition?.text =
        (exhibition.startDate?.getDateExhibition() + " - " + exhibition.endDate?.getDateExhibition())
            .toUpperCase()

    tv_desc_exhibition?.text = exhibition.description

}