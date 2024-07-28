package com.example.todolist.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.SaveList
import com.example.todolist.model.ToDoList
import com.example.todolist.utills.SingleLiveEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ListViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val dbCourse = auth.currentUser?.uid?.let { db.collection(it) }
    val taskList = MutableLiveData<ArrayList<ToDoList>>()
    val addNotes = SingleLiveEvent<Void>()

    fun addNotesOnClick() {
        addNotes.call()
    }

    fun deleteList(keyId: String) {
        dbCourse?.document(keyId)?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) {
//                Toast.makeText(this@ListActivity, "data deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this@ListActivity, "data not delete", Toast.LENGTH_SHORT).show();
            }
        }
    }
    fun createList(list: SaveList) {
        dbCourse?.add(list)?.addOnCompleteListener {
            if (it.isSuccessful) {
//                Toast.makeText(context, "list added Successfully", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "list not added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun readData() {
        dbCourse?.get()?.addOnCompleteListener { it ->
            if (it.isSuccessful) {

                it.addOnSuccessListener { queryDocumentSnapshots ->
                    val tempList = ArrayList<ToDoList>()

                    queryDocumentSnapshots?.forEach { document ->
                        val id = document.id
                        val todoList = document.toObject(ToDoList::class.java)
                        todoList.id = id
                        tempList.add(todoList)
                    }
                    taskList.postValue(tempList)
                }
            }
        }
    }
}
