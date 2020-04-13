package com.zailaapp.zaila.views.fragments.artworkList.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.projectOnly.fillArtwork
import com.zailaapp.zaila.model.responses.Artwork
import kotlinx.android.synthetic.main.row_artwork.view.*

class ArtworkAdapter(
    private val list: List<Artwork>,
    private val clickListener: (Artwork) -> Unit
) : RecyclerView.Adapter<ArtworkAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_artwork,
                parent, false
            )

        return VH(
            v
        )
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

        fun bind(artwork: Artwork, listener: (Artwork) -> Unit) {

            itemView.row_artwork?.fillArtwork(artwork)

            itemView.row_artwork.setOnClickListener {
                listener(artwork)
            }

        }
    }
}