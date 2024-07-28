package com.example.todolist.ui.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.SaveList
import com.example.todolist.utills.SingleLiveEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UpdateListViewModel : ViewModel() {
    val titleTxt = MutableLiveData<String>()
    val descriptionTxt = MutableLiveData<String>()
    val typeTxt = MutableLiveData<String>()
    val saveListBtn = SingleLiveEvent<Void>()
    val datePickerBtn = SingleLiveEvent<Void>()
    val timePickerBtn = SingleLiveEvent<Void>()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val dbCourse = auth.currentUser?.uid?.let { db.collection(it) }


    fun onSaveBtnClick() {
        saveListBtn.call()
    }
    fun onDatePickerBtnClick(){
        datePickerBtn.call()
    }
    fun onTimePickerBtnClick(){
        timePickerBtn.call()
    }
    fun createList(list: SaveList, context: Context) {
        dbCourse?.add(list)?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "list added Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "list not added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun updateData(keyId: String, updatedList: SaveList,context: Context) {
        dbCourse?.document(keyId)?.set(updatedList)?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "list updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "list not updated", Toast.LENGTH_SHORT).show();
            }
        }
    }
}