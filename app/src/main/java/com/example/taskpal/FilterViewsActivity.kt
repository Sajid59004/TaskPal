package com.example.taskpal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpal.adapter.TaskAdapter
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FilterViewsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_views)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFilteredTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(context = this)
        recyclerView.adapter = taskAdapter

        // Load all tasks initially
        loadFilteredTasks(null)

        val btnLowPriority = findViewById<Button>(R.id.btnLowPriority)
        val btnNormalPriority = findViewById<Button>(R.id.btnNormalPriority)
        val btnUrgentPriority = findViewById<Button>(R.id.btnUrgentPriority)

        btnLowPriority.setOnClickListener {
            loadFilteredTasks("Low")
        }

        btnNormalPriority.setOnClickListener {
            loadFilteredTasks("Normal")
        }

        btnUrgentPriority.setOnClickListener {
            loadFilteredTasks("Urgent")
        }

        val imageViewHome = findViewById<ImageView>(R.id.home)
        val imageViewInbox = findViewById<ImageView>(R.id.inbox)
        val imageViewCalendarView = findViewById<ImageView>(R.id.CalendarView)
        val imageViewProject = findViewById<ImageView>(R.id.project)
        val goBack = findViewById<Button>(R.id.button6)

        imageViewHome.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, HomeActivity::class.java))
        }

        imageViewInbox.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, TaskInbox::class.java))
        }

        imageViewCalendarView.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, CalendarViewActivity::class.java))
        }

        imageViewProject.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, ProjectActivity::class.java))
        }

        goBack.setOnClickListener {
            finish()
        }
    }

    private fun loadFilteredTasks(priority: String?) {
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.email ?: return

        // Build the query
        var query = db.collection("tasks").whereEqualTo("userId", userId)

        // Add priority filter if it's not null
        if (priority != null) {
            query = query.whereEqualTo("priority", priority)
        }

        // Execute the query
        query.get()
            .addOnSuccessListener { documents ->
                val tasks = documents.map { document ->
                    document.toObject(TaskModel::class.java)
                }
                taskAdapter.setTasks(tasks)
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

}
