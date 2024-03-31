package com.example.go4lunch24kotlin.services


import com.example.go4lunch24kotlin.models.poko.NearbySearchResults
import com.example.go4lunch24kotlin.models.poko.Predictions
import com.example.go4lunch24kotlin.models.poko.RestaurantDetailsResult
import retrofit2.http.GET

import retrofit2.Call

import retrofit2.http.Query



interface GooglePlacesService {

    @GET("maps/api/place/nearbysearch/json")
    fun searchRestaurant(
        @Query("key") key: String?,
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: String?
    ): Call<NearbySearchResults?>?

    @GET("maps/api/place/details/json")
    fun searchRestaurantDetails(
        @Query("key") key: String?,
        @Query("place_id") place_id: String?,
        @Query("fields") fields: String?
    ): Call<RestaurantDetailsResult?>?

    @GET("maps/api/place/autocomplete/json")
    fun autocompleteResult(
        @Query("key") key: String?,
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: String?,
        @Query("input") input: String?
    ): Call<Predictions?>?
}

