package com.example.todolist.fcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.R
import com.example.todolist.ui.activity.ListActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FireBaseMessageReceive: FirebaseMessagingService() {
    private val CHANNEL_ID = "My Channel"
    private val NOTIFICATION_ID = 100

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("XXX", "new fcm token $token")
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
           val intent = Intent(this,ListActivity::class.java)
                startActivity(intent)
            val pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            createNotificationChannels()
            val messageBody = it.body
            val messageTitle = it.title
            val builder = NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_todo)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            if (checkNotificationPermission()){
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID,builder.build())
            }
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun createNotificationChannels() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel =   NotificationChannel(CHANNEL_ID,"My Channel",NotificationManager.IMPORTANCE_HIGH)
             getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        }
    }


}