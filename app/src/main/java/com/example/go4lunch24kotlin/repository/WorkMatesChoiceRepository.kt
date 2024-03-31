package com.example.go4lunch24kotlin.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.time.Clock
import java.time.LocalDate

class WorkMatesChoiceRepository (
    private val clock: Clock,
) {

    private val db = FirebaseFirestore.getInstance()

    fun getWorkmatesRestaurantChoice(): LiveData<List<WorkMateRestaurantChoice>> {

        val userModelMutableLiveData = MutableLiveData<List<WorkMateRestaurantChoice>>()

        val today = LocalDate.now(clock)

        val usersWithRestaurant = mutableListOf<WorkMateRestaurantChoice>()

        db.collection(today.toString())
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("restaurant choice error", error.message!!)
                    return@addSnapshotListener
                }
                assert(value != null)
                for (document in value!!.documentChanges) {

                    Log.d("pipo",
                        "onEvent() called with: value = [" + document.document.toObject(WorkMateRestaurantChoice::class.java) + "], error = [" + null + "]")

                    when (document.type) {
                        DocumentChange.Type.ADDED -> {
                            usersWithRestaurant.add(document.document.toObject(
                                WorkMateRestaurantChoice::class.java))
                        }
                        DocumentChange.Type.MODIFIED -> {
                            usersWithRestaurant.removeIf { user ->
                                user.userId == document.document.toObject(WorkMateRestaurantChoice::class.java).userId
                            }
                            usersWithRestaurant.add(document.document.toObject(WorkMateRestaurantChoice::class.java))
                        }
                        DocumentChange.Type.REMOVED -> {
                            usersWithRestaurant.remove(document.document.toObject(WorkMateRestaurantChoice::class.java))
                        }
                    }
                }
                userModelMutableLiveData.setValue(usersWithRestaurant)
            }
        return userModelMutableLiveData
    }
}