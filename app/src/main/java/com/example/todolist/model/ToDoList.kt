package com.example.todolist.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ToDoList(
    var id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val date: String? = null,
    val day: String? = null,
    val type: String? = null,
    val priority: Int? = null,
    val dateWithDay:String? = null,
    val priorityInWords: String? =null
) : Parcelable

@Parcelize
data class SaveList(
    val title: String? = null,
    val description: String? = null,
    val date: String? = null,
    val day: String? = null,
    val type: String? = null,
    val priority: Int? = null,
    val dateWithDay:String? = null,
    val priorityInWords: String? =null
) : Parcelable
