package com.zailaapp.zaila.views.fragments.museumDetails

import androidx.lifecycle.MutableLiveData
import com.zailaapp.zaila.data.api.zaila.getApiMessage
import com.zailaapp.zaila.model.responses.Museum
import com.zailaapp.zaila.model.responses.MuseumResponse
import com.zailaapp.zaila.repositories.MuseumRepository
import com.zailaapp.zaila.views.base.BaseViewModel
import kotlinx.coroutines.launch
import org.koin.core.inject

class MuseumDetailsViewModel : BaseViewModel() {

    //RepositoryModule.kt maps the injection for this:
    private val museumRepository: MuseumRepository by inject()

    //region LiveData

    /* Here we declare mutable properties that will be observed by the frontend: */
    val museum: MutableLiveData<Museum> by lazy {
        MutableLiveData<Museum>()
    }

    //endregion LiveData

    //region public methods
    fun getMuseum(museumId: Int) {

        //Run this in a background thread:
        bgScope.launch {

            //Show loading:
            isLoading.postValue(museum.value == null)

            var museumResponse: MuseumResponse? = null

            try {

                // If success, then fill artwork object:
                museumResponse = museumRepository.getMuseum(museumId)

            } catch (throwable: Throwable) {

                val apiMessage =
                    throwable.getApiMessage() //Extension function to read the api response error

                // FAIL - Assigns the api error to the "errorMessage" mutable property.
                errorMessage.postValue(apiMessage)
            }

            //Hides loading:
            isLoading.postValue(false)

            if (museumResponse != null) {

                museum.postValue(museumResponse.museum)
            }

        }
    }
    //endregion public methods
}