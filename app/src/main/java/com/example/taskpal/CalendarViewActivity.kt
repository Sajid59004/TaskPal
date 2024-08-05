package com.example.taskpal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpal.R
import com.example.taskpal.adapter.TaskAdapter
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CalendarViewActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var taskRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        taskRecyclerView = findViewById(R.id.taskRecyclerView)

        // Set up RecyclerView
        adapter = TaskAdapter(context = this)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter

        loadTasks()
    }

    private fun loadTasks() {
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.email ?: return

        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = documents.map { document ->
                    document.toObject(TaskModel::class.java)
                }
                adapter.setTasks(tasks)
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}
