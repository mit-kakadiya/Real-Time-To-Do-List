package com.example.todolist.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import com.example.todolist.databinding.ActivityAuthenticationBinding



class AuthenticationActivity : BaseActivity<ActivityAuthenticationBinding>() {

    private val NOTIFICATION_PERMISSION_CODE = 1001
    override fun inflateLayout(layoutInflater: LayoutInflater): ActivityAuthenticationBinding {
       return ActivityAuthenticationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        requestNotificationPermission()
    }

    override fun setUpClickEvents() {

    }

    override fun onObservers() {

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Permission", "Notification permission granted")
            } else {
                Log.d("Permission", "Notification permission denied")
            }
        }
    }
}