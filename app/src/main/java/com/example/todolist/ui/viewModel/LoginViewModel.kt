package com.example.todolist.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.utills.SingleLiveEvent

class LoginViewModel:ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val visibility = SingleLiveEvent<Void>()

    fun onVisibilityClick(){
        visibility.call()
    }
}