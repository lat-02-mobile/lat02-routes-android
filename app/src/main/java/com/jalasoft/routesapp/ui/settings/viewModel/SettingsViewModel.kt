package com.jalasoft.routesapp.ui.settings.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor() : ViewModel() {

    companion object {
        const val TAG = "Settings"
    }

    val db = Firebase.firestore

    fun readData() {
        db.collection("Countries").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data} ")
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)
        }
    }
}
