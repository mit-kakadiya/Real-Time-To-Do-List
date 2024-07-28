package com.example.todolist.listeners

import com.example.todolist.model.ToDoList

interface ClickOnTaskListener {
    fun onTaskSelected(list:ToDoList)
}