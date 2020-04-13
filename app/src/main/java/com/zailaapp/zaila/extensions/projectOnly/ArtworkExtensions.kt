package com.zailaapp.zaila.extensions.projectOnly

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.loadRoundArtwork
import com.zailaapp.zaila.model.responses.Artwork

fun View.fillArtwork(artwork: Artwork) {

    val img_artwork = findViewById<ImageView>(R.id.img_artwork)
    artwork.imageURL?.let { img_artwork?.loadRoundArtwork(it) }

    val tv_title_artwork = findViewById<TextView>(R.id.tv_title_artwork)
    val tv_artist_artwork = findViewById<TextView>(R.id.tv_artist_artwork)
    val tv_media_artwork = findViewById<TextView>(R.id.tv_media_artwork)
    val tv_media_year_artwork = findViewById<TextView>(R.id.tv_media_year_artwork)

    tv_title_artwork?.text = artwork.title
    tv_artist_artwork?.text = artwork.artistName
    tv_media_year_artwork?.text = "${artwork.media} - ${artwork.year}"
}