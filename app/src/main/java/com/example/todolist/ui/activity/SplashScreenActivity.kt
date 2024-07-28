package com.example.todolist.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.todolist.R
import com.example.todolist.common.Extension.runDelayedOnMainThread
import com.example.todolist.common.constants.Constants
import kotlinx.coroutines.time.delay


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var sh :SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
       val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{true}
        runDelayedOnMainThread(2000){
            pushToOnNextScreen()
            finish()
        }
    }

    private fun pushToOnNextScreen() {
       val isLogin =  sh?.getBoolean(Constants.IS_LOGIN,false)
        if (isLogin == true) {
            //push homeScreen
            Intent(this,ListActivity::class.java).apply {
                startActivity(this)
                finishAffinity()
            }
        } else {
            //push loginScreen
            Intent(this,AuthenticationActivity::class.java).apply {
                startActivity(this)
                finishAffinity()
            }
        }
    }
}