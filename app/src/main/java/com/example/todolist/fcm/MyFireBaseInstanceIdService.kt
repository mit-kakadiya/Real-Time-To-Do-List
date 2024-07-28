package com.example.todolist.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFireBaseInstanceIdService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("XXX",token)
    }

}