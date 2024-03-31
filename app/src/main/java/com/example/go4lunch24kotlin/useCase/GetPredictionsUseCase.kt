package com.example.go4lunch24kotlin.useCase
/*
import android.location.Location
import androidx.lifecycle.LiveData
import com.example.go4lunch24kotlin.models.poko.Predictions
import com.example.go4lunch24kotlin.repository.AutocompleteRepository
import com.example.go4lunch24kotlin.repository.LocationRepository

class GetPredictionsUseCase (
    private val locationRepository: LocationRepository,
    private val autocompleteRepository: AutocompleteRepository,
) {

    // RETRIEVE AUTOCOMPLETE PREDICTIONS RESULTS FROM LOCATION
    fun invoke(text: String?) : LiveData<Predictions> =
        Transformations.switchMap(locationRepository.getLocationLiveData()) { input: Location ->
            val locationAsText = input.latitude.toString() + "," + input.longitude
            Transformations.map(autocompleteRepository.getAutocompleteResultListLiveData(
                locationAsText,
                text)
            ) { input1: Predictions? -> input1 }
        }
}

 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.go4lunch24kotlin.models.poko.Predictions
import com.example.go4lunch24kotlin.repository.AutocompleteRepository
import com.example.go4lunch24kotlin.repository.LocationRepository

class GetPredictionsUseCase(
    private val locationRepository: LocationRepository,
    private val autocompleteRepository: AutocompleteRepository,
) {

    fun invoke(text: String?): LiveData<Predictions> {
        val predictionsResult = MediatorLiveData<Predictions>()

        val locationLiveData = locationRepository.getLocationLiveData()

        predictionsResult.addSource(locationLiveData) { location ->
            val locationAsText = "${location?.latitude},${location?.longitude}"

            // Il est important de retirer la source précédente pour éviter des appels multiples et des fuites de mémoire.
            // Notez que dans ce cas, nous supposons que `autocompleteRepository.getAutocompleteResultListLiveData`
            // renvoie un nouveau LiveData à chaque appel. Si ce n'est pas le cas, vous devriez ajuster cette logique.
            val autocompleteLiveData = autocompleteRepository.getAutocompleteResultListLiveData(locationAsText, text)
            predictionsResult.addSource(autocompleteLiveData) { predictions ->
                predictions?.let {
                    predictionsResult.value = it
                    // Retirez la source après avoir obtenu une mise à jour pour éviter des appels redondants.
                    predictionsResult.removeSource(autocompleteLiveData)
                }
            }
        }

        return predictionsResult
    }
}
