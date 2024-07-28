package com.example.todolist.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.example.todolist.R
import com.example.todolist.ui.activity.ListActivity


class NotificationBroadCastReceiver:BroadcastReceiver(){
    private val CHANNEL_ID = "My Channel"
    private val NOTIFICATION_ID = 100

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            sendNotification(context)
        }
    }

    private fun sendNotification(context: Context){
        val intent = Intent(context, ListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context,intent,null)
        val pendingIntent = PendingIntent.getActivity(context,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        createNotificationChannels(context)
        val messageBody = "Due Date is Comming!!"
        val messageTitle = "ToDo List"
        val builder = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_todo)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (checkNotificationPermission(context)){
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,builder.build())
        }
    }

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =   NotificationChannel(CHANNEL_ID,"My Channel", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(context,NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }
}