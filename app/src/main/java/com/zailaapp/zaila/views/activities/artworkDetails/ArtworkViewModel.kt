package com.zailaapp.zaila.views.activities.artworkDetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zailaapp.zaila.data.api.zaila.getApiMessage
import com.zailaapp.zaila.model.responses.Artwork
import com.zailaapp.zaila.model.responses.ArtworkDetails
import com.zailaapp.zaila.repositories.ArtworkRepository
import com.zailaapp.zaila.views.base.BaseViewModel
import kotlinx.coroutines.launch
import org.koin.core.inject

class ArtworkViewModel : BaseViewModel() {

    //RepositoryModule.kt maps the injection for this:
    private val artworkRepository: ArtworkRepository by inject()

    //region LiveData

    /* Here we declare mutable properties that will be observed by the frontend: */
    val artwork: MutableLiveData<Artwork> by lazy {
        MutableLiveData<Artwork>()
    }

    val artworkDetails: MutableLiveData<List<ArtworkDetails>> by lazy {
        MutableLiveData<List<ArtworkDetails>>()
    }

    //endregion LiveData

    //region public methods
    fun getArtwork(sensorId: String) {

        //Run this in a background thread:
        bgScope.launch {

            //Show loading:
            isLoading.postValue(true)

            var artworkResponse: Artwork? = null

            try {

                // If success, then fill artwork object:
                artworkResponse = artworkRepository.getArtwork(sensorId)

            } catch (throwable: Throwable) {

                val apiMessage =
                    throwable.getApiMessage() //Extension function to read the api response error

                // FAIL - Assigns the api error to the "errorMessage" mutable property.
                errorMessage.postValue(apiMessage)
            }

            //Hides loading:
            isLoading.postValue(false)

            if (artworkResponse != null) {

                artwork.postValue(artworkResponse)
                artworkDetails.postValue(artworkResponse.artworkDetails)
            }

        }
    }
    //endregion public methods
}