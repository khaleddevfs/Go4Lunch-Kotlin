package com.example.go4lunch24kotlin.useCase


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class GetCurrentUserUseCase {

    companion object{
        fun getFirebaseAuth() : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    }
}