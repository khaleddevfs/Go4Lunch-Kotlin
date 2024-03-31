package com.example.go4lunch24kotlin.useCase

import com.google.firebase.auth.FirebaseAuth

class GetCurrentUserIdUseCase {
    operator fun invoke(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}