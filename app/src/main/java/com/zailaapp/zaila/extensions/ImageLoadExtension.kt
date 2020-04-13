package com.zailaapp.zaila.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.zailaapp.zaila.utils.OldRoundedBitmapDisplayer


fun ImageView.load(url: String, failImage: Int = 0, placeholder: Int = 0) {

    val optionsBuilder = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnLoading(placeholder)

    if (failImage > 0) {
        optionsBuilder.showImageOnFail(
            BitmapDrawable(
                context.resources,
                getBitmapFromVectorDrawable(context, failImage)
            )
        )
    }

    val options = optionsBuilder.build()

    ImageLoader.getInstance().displayImage(url, this, options)
}

/**
 * Loads a round image. Don't forget:
 * android:scaleType="centerCrop"
 *
 * Uses a custom bitmap displayer to make the centerCrop work!!!
 */
fun ImageView.loadRound(url: String, failImage: Int = 0, placeholder: Int = 0) {

    Glide.with(context)
        .load(url)
        .override(300, 300)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(RequestOptions.circleCropTransform())
        .into(this);
}

fun ImageView.loadRoundFromVector(resId: Int) {

    Glide.with(context)
        .load(resId)
        .override(300, 300)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(RequestOptions.circleCropTransform())
        .into(this);
}

fun ImageView.loadRound(url: String, round: Int) {

    val x = this

    val optionsBuilder = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .displayer(OldRoundedBitmapDisplayer(12))

    val options = optionsBuilder.build()

    ImageLoader.getInstance().displayImage(url, this, options)


//    val r = RequestOptions()
//    r.transform(RoundedCorners(round))
//    Glide.with(context)
//        .load(url)
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .apply(r)
////        .apply(RequestOptions.circleCropTransform())
//        .into(this);
}

fun ImageView.loadRoundArtwork(url: String, failImage: Int = 0, placeholder: Int = 0) {

    val r = RequestOptions()
    r.transform(RoundedCorners(50))
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(r)
//        .apply(RequestOptions.circleCropTransform())
        .into(this);
}

fun ImageView.setBitmapFromVector(drawableId: Int) {
    val drawable = ContextCompat.getDrawable(context, drawableId)

    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    setImageBitmap(bitmap)
}

private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)

    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}