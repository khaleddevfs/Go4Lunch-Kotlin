package com.example.go4lunch24kotlin.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.models.Workmate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import java.util.HashSet

class WorkMatesRepository private constructor() {
    private var workMate: Workmate? = null
    private val workMateCollection: CollectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

    companion object {
        private const val COLLECTION_NAME = "workMate" // Adjust this based on your Firestore collection name
        const val USERS = "users"
        const val USER_NAME = "userName"
      //  @Volatile
        private var instance: WorkMatesRepository? = null

        fun getInstance(): WorkMatesRepository {
            if (instance == null) {
                instance = WorkMatesRepository()
            }
            return instance!!
        }

    }

    fun getActualUser(): Workmate? = workMate

    fun createWorkmate(workMate: Workmate?): Task<Void> {
        return workMate?.let {
            this.workMate = it
            it.uid?.let { it1 -> workMateCollection.document(it1).set(it) }
        } ?: Tasks.forException(NullPointerException("WorkMate object is null."))
    }

    fun getWorkMateFromFirebase(uid: String): Task<DocumentSnapshot> = workMateCollection.document(uid).get()

    fun getAllWorkMate(): Task<QuerySnapshot> = workMateCollection.get()

    fun updateRestaurantPicked(id: String, name: String, address: String, userUid: String): Task<Void> {
        return workMate?.let {
            val choice = WorkMateRestaurantChoice(id, name, address)
            it.workMateRestaurantChoice = choice
            workMateCollection.document(userUid).update("workMateRestaurantChoice", choice)
        } ?: Tasks.forException(NullPointerException("WorkMate object is null."))
    }

    fun addLikedRestaurant(likedRestaurant: String): Task<Void> {
        return workMate?.let {
            it.addLikedRestaurant(likedRestaurant)
            it.uid?.let { it1 -> updateLikedRestaurants(it1) }
        } ?: Tasks.forException(NullPointerException("WorkMate object is null."))
    }

    fun removeLikedRestaurant(likedRestaurant: String): Task<Void> {
        return workMate?.let {
            it.removeLikedRestaurant(likedRestaurant)
            it.uid?.let { it1 -> updateLikedRestaurants(it1) }
        } ?: Tasks.forException(NullPointerException("WorkMate object is null."))
    }

    private fun updateLikedRestaurants(uid: String): Task<Void> {
        val likedRestaurantList = workMate?.likedRestaurants
        return workMateCollection.document(uid).update("likedRestaurants", likedRestaurantList)
    }

    fun updateCurrentUser(workMate: Workmate) {
        this.workMate = workMate
    }

    fun getWorkmates(): LiveData<List<Workmate>> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userModelMutableLiveData = MutableLiveData<List<Workmate>>()
        val workmates: MutableSet<Workmate> = HashSet()

        FirebaseFirestore.getInstance().collection(USERS)
            .orderBy(USER_NAME)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println("Firestore error: ${error.message}")
                    userModelMutableLiveData.value = emptyList()
                    return@addSnapshotListener
                }
                value?.documentChanges?.forEach { documentChange ->
                    documentChange.document.toObject(Workmate::class.java).let { workmate ->
                        if (userId != workmate.uid) {
                            if (documentChange.type == DocumentChange.Type.ADDED ||
                                documentChange.type == DocumentChange.Type.MODIFIED) {
                                workmates.add(workmate)
                            }
                        }
                    }
                }
                userModelMutableLiveData.value = ArrayList(workmates)
            }
        return userModelMutableLiveData
    }
}
