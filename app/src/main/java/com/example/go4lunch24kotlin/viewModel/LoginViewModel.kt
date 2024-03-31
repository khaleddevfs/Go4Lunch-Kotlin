package com.example.go4lunch24kotlin.viewModel


import androidx.lifecycle.ViewModel
import android.app.Activity
import android.util.Log
import com.example.go4lunch24kotlin.models.Workmate
import com.example.go4lunch24kotlin.repository.WorkMatesRepository

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.util.Collections

class LoginViewModel(private val workMatesRepository: WorkMatesRepository) : ViewModel() {

    companion object {
        const val RC_SIGN_IN = 100
        var workmate: Workmate? = null

    }

    fun startLoginActivityEmail(activity: Activity) {
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(),
            RC_SIGN_IN
        )
    }

    fun startLoginActivityGoogle(activity: Activity) {
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(),
            RC_SIGN_IN
        )
    }

    fun startLoginActivityTwitter(activity: Activity) {
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(AuthUI.IdpConfig.TwitterBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(),
            RC_SIGN_IN
        )
    }

    fun updateCurrentUser() {
        val uid = if (isCurrentUserLogged()) getCurrentUser()?.uid ?: "default" else return // Utilisez return pour éviter TODO() qui lance une exception
        workMatesRepository.getWorkMateFromFirebase(uid)
            .addOnSuccessListener { documentSnapshot ->
                workmate = documentSnapshot.toObject(Workmate::class.java)
                workmate?.let {
                    workMatesRepository.updateCurrentUser(it)
                } ?: createUserInFirestore()
            }
            .addOnFailureListener { error ->
                Log.e("tagii", "error: $error")
            }
    }


    private fun createUserInFirestore() {
        val currentUser = getCurrentUser()

        if (currentUser != null) {
            val uid = currentUser.uid
            val name = currentUser.displayName
            val email = currentUser.email
            val urlPicture = currentUser.photoUrl?.toString()

            val workMate1 = Workmate(uid, name, email, urlPicture)
            workMatesRepository.createWorkmate(workMate1)
                .addOnSuccessListener { workMatesRepository.updateCurrentUser(workMate1) }
                .addOnFailureListener { error -> Log.e("tagii", "error create user: $error") }
        } else {
            Log.e("tagii", "Current user is null in createUserInFirestore")
        }
    }

    private fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    fun isCurrentUserLogged() = getCurrentUser() != null
}
