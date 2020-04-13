package com.zailaapp.zaila.data.api.places

interface PlacesApi {
    suspend fun getNearbyMuseums(lat: String, lng: String): List<PlaceDetail>
}