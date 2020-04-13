package com.zailaapp.zaila.views.fragments.artworkList

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zailaapp.zaila.R
import com.zailaapp.zaila.extensions.gone
import com.zailaapp.zaila.extensions.vis
import com.zailaapp.zaila.model.responses.Artwork
import com.zailaapp.zaila.repositories.ArtworkRepository
import com.zailaapp.zaila.views.activities.artworkDetails.ArtworkDetailsActivity
import com.zailaapp.zaila.views.base.BaseFragment
import com.zailaapp.zaila.views.fragments.artworkList.adapters.ArtworkAdapter
import kotlinx.android.synthetic.main.fragment_artwork_list.*
import kotlinx.android.synthetic.main.layout_progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ArtworkListFragment : BaseFragment(R.layout.fragment_artwork_list) {

    private val _artworkRepository: ArtworkRepository by inject()

    private val _navController by lazy { findNavController() }

    //region initialization
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        progress.vis()

        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {

            val x = _artworkRepository.getArtwork()

            activity?.runOnUiThread {

                progress?.gone()
                setupArtworkList(x.map {
                    it.artwork!!
                })
            }
        } //End CoroutineScope
    }

    //endregion initialization


    //region setup list
    private fun setupArtworkList(list: List<Artwork>) {

        if (recyclerView_artworks == null) return

        val adapter =
            ArtworkAdapter(
                list
            )
            {
                startActivity(ArtworkDetailsActivity.newIntent(it.sensorId!!))
            }

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView_artworks?.layoutManager = llm

        recyclerView_artworks?.adapter = adapter
    }

    //endregion setup list
}
