package com.example.taskpal.repository

import com.example.taskpal.model.TaskModel
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    fun addTask(task: TaskModel) {
        tasksCollection.add(task)
            .addOnSuccessListener { documentReference ->
                // Task added successfully
                val taskId = documentReference.id
                // Optionally, perform any additional actions after adding the task
            }
            .addOnFailureListener { e ->
                // Failed to add task
                // Handle the error
            }
    }
}
