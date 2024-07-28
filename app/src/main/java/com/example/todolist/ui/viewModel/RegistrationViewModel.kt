package com.example.todolist.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.utills.SingleLiveEvent

class RegistrationViewModel:ViewModel(){
    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val passwordVisible = SingleLiveEvent<Void>()
    val cPasswordVisible = SingleLiveEvent<Void>()

    fun onPasswordVisible(){
        passwordVisible.call()
    }

    fun onCPasswordVisible(){
        cPasswordVisible.call()
    }
}