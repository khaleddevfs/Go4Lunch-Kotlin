package com.example.go4lunch24kotlin.factory

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.go4lunch24kotlin.MainApplication
import com.example.go4lunch24kotlin.repository.AutocompleteRepository
import com.example.go4lunch24kotlin.repository.FavoriteRestaurantRepository
import com.example.go4lunch24kotlin.repository.LocationRepository
import com.example.go4lunch24kotlin.repository.NearbySearchRepository
import com.example.go4lunch24kotlin.repository.NotificationsRepository
import com.example.go4lunch24kotlin.repository.RestaurantDetailsRepository
import com.example.go4lunch24kotlin.repository.UserSearchRepository
import com.example.go4lunch24kotlin.repository.WorkMatesChoiceRepository
import com.example.go4lunch24kotlin.repository.WorkMatesRepository
import com.example.go4lunch24kotlin.services.GooglePlacesService
import com.example.go4lunch24kotlin.useCase.ClickOnChoseRestaurantButtonUseCase
import com.example.go4lunch24kotlin.useCase.ClickOnFavoriteRestaurantUseCase
import com.example.go4lunch24kotlin.useCase.GetCurrentUserIdUseCase
import com.example.go4lunch24kotlin.useCase.GetNearbySearchResultsByIdUseCase
import com.example.go4lunch24kotlin.useCase.GetNearbySearchResultsUseCase
import com.example.go4lunch24kotlin.useCase.GetPredictionsUseCase
import com.example.go4lunch24kotlin.useCase.GetRestaurantDetailsResultsByIdUseCase
import com.example.go4lunch24kotlin.useCase.GetRestaurantDetailsResultsUseCase
import com.example.go4lunch24kotlin.viewModel.LoginViewModel
import com.example.go4lunch24kotlin.viewModel.MainActivityViewModel
import com.example.go4lunch24kotlin.viewModel.MapViewModel
import com.example.go4lunch24kotlin.viewModel.RestaurantDetailsViewModel
import com.example.go4lunch24kotlin.viewModel.RestaurantsViewModel
import com.example.go4lunch24kotlin.viewModel.SettingViewModel
import com.example.go4lunch24kotlin.viewModel.WorkMatesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalArgumentException
import java.time.Clock


@Suppress("UNCHECKED_CAST")
class Go4LunchFactory: ViewModelProvider.Factory {


    private val application: Application
    private val context: Context
    private val locationRepository: LocationRepository
    private val workMatesRepository: WorkMatesRepository
    private val workMatesChoiceRepository : WorkMatesChoiceRepository
    private val mUserSearchRepository: UserSearchRepository
    private val favoriteRestaurantRepository: FavoriteRestaurantRepository
    private val notificationsRepository: NotificationsRepository
    private val getNearbySearchResultsUseCase: GetNearbySearchResultsUseCase
    private val getNearbySearchResultsByIdUseCase: GetNearbySearchResultsByIdUseCase
    private val getRestaurantDetailsResultsUseCase: GetRestaurantDetailsResultsUseCase
    private val getRestaurantDetailsResultsByIdUseCase: GetRestaurantDetailsResultsByIdUseCase
    private val getPredictionsUseCase: GetPredictionsUseCase
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
    private val clickOnChoseRestaurantButtonUseCase: ClickOnChoseRestaurantButtonUseCase
    private val clickOnFavoriteRestaurantUseCase: ClickOnFavoriteRestaurantUseCase


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var factory: Go4LunchFactory? = null
        val instance: Go4LunchFactory?
            get() {
                if (factory == null) {
                    synchronized(Go4LunchFactory::class.java) {
                        if (factory == null) {
                            factory = Go4LunchFactory()
                        }
                    }
                }
                return factory
            }
    }

    init {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val googleMapsApi = retrofit.create(GooglePlacesService::class.java)

        val firebaseFirestore = FirebaseFirestore.getInstance()

        val firebaseAuth = FirebaseAuth.getInstance()

      //  application = MainApplication.getApplication()!!
        application = MainApplication.getApplication()!!
        context = application.applicationContext
        val nearbySearchRepository = NearbySearchRepository(googleMapsApi, application)
        val restaurantDetailsRepository = RestaurantDetailsRepository(googleMapsApi, application)
        val autocompleteRepository = AutocompleteRepository(googleMapsApi, application)

        locationRepository = LocationRepository()
        workMatesRepository = WorkMatesRepository.getInstance()
        mUserSearchRepository = UserSearchRepository()
        favoriteRestaurantRepository = FavoriteRestaurantRepository()
        workMatesChoiceRepository = WorkMatesChoiceRepository(Clock.systemDefaultZone())
        notificationsRepository = NotificationsRepository(context)
        getNearbySearchResultsUseCase = GetNearbySearchResultsUseCase(locationRepository, nearbySearchRepository, application)
        getNearbySearchResultsByIdUseCase = GetNearbySearchResultsByIdUseCase(locationRepository, nearbySearchRepository, application)
        getRestaurantDetailsResultsUseCase = GetRestaurantDetailsResultsUseCase(locationRepository, nearbySearchRepository, restaurantDetailsRepository, application)
        getRestaurantDetailsResultsByIdUseCase = GetRestaurantDetailsResultsByIdUseCase(restaurantDetailsRepository)
        getPredictionsUseCase = GetPredictionsUseCase(locationRepository, autocompleteRepository)
        getCurrentUserIdUseCase = GetCurrentUserIdUseCase()
        clickOnChoseRestaurantButtonUseCase = ClickOnChoseRestaurantButtonUseCase(firebaseFirestore, firebaseAuth, Clock.systemDefaultZone())
        clickOnFavoriteRestaurantUseCase = ClickOnFavoriteRestaurantUseCase(firebaseFirestore)
    }



    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        when {
            modelClass.isAssignableFrom(RestaurantsViewModel::class.java) -> {
                return RestaurantsViewModel(
                    application,
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    getRestaurantDetailsResultsUseCase,
                    workMatesChoiceRepository,
                    mUserSearchRepository,
                    Clock.systemDefaultZone()) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                return MapViewModel(
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    workMatesChoiceRepository,
                    mUserSearchRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(workMatesRepository) as T
            }

            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                return MainActivityViewModel(
                    application,
                    locationRepository,
                    getPredictionsUseCase,
                    mUserSearchRepository,
                    workMatesChoiceRepository,
                    getCurrentUserIdUseCase
                ) as T
            }
            modelClass.isAssignableFrom(RestaurantDetailsViewModel::class.java) -> {
                return RestaurantDetailsViewModel(
                    application,
                    getNearbySearchResultsByIdUseCase,
                    getRestaurantDetailsResultsByIdUseCase,
                    workMatesChoiceRepository,
                    workMatesRepository,
                    favoriteRestaurantRepository,
                    getCurrentUserIdUseCase,
                    clickOnChoseRestaurantButtonUseCase,
                    clickOnFavoriteRestaurantUseCase) as T
            }
            modelClass.isAssignableFrom(WorkMatesViewModel::class.java) -> {
                return WorkMatesViewModel(
                    application,
                    workMatesRepository,
                    workMatesChoiceRepository
                ) as T
            }

            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                return SettingViewModel(
                    notificationsRepository,
                    context,
                    Clock.systemDefaultZone()
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}