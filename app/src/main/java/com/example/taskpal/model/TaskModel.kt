package com.example.taskpal.model

data class TaskModel(
    var id: String = "",
    var taskName: String = "",
    var taskDescription: String = "",
    var taskStatus: String? = null,
    var userId: String = "",
    var priority: String = "",
    var dueDate: String = "",
    var location: String = ""
)
