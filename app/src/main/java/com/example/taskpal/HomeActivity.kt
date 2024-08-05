package com.example.taskpal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpal.model.TaskModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Receive the selected color from the previous activity
        val themeColor = intent.getIntExtra("themeColor", R.color.colorAccent1)

        // Apply the selected color to the theme
        when (themeColor) {
            R.color.colorAccent1 -> setTheme(R.style.Theme_TaskPal_Accent1)
            R.color.colorAccent2 -> setTheme(R.style.Theme_TaskPal_Accent2)
            R.color.colorAccent3 -> setTheme(R.style.Theme_TaskPal_Accent3)
            R.color.colorAccent4 -> setTheme(R.style.Theme_TaskPal_Accent4)
            else -> setTheme(R.style.Theme_TaskPal_Accent1) // Default theme
        }

        findViewById<ImageView>(R.id.imageViewAdd).setBackgroundColor(themeColor)

        // Initialize UI components
        val materialCardView = findViewById<MaterialCardView>(R.id.materialCardView)
        val imageViewSettings = findViewById<ImageView>(R.id.settings)
        val imageViewInbox = findViewById<ImageView>(R.id.inbox)
        val imageViewCalendarView = findViewById<ImageView>(R.id.CalendarView)
        val imageViewFilterViews = findViewById<ImageView>(R.id.FilterViews)
        val imageViewProject = findViewById<ImageView>(R.id.project)

        // Set onClickListeners
        materialCardView.setOnClickListener {
            showAddTaskDialog()
        }

        imageViewSettings.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AccountSetting::class.java))
        }

        imageViewInbox.setOnClickListener {
            startActivity(Intent(this@HomeActivity, TaskInbox::class.java))
        }

        imageViewCalendarView.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CalendarViewActivity::class.java))
        }

        imageViewFilterViews.setOnClickListener {
            startActivity(Intent(this@HomeActivity, FilterViewsActivity::class.java))
        }

        imageViewProject.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProjectActivity::class.java))
        }
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.popup_layout, null)
        builder.setView(dialogView)

        val editTextTask = dialogView.findViewById<EditText>(R.id.editTaskName)
        val editTextTaskDescription = dialogView.findViewById<EditText>(R.id.editTaskDescription)
        val editTextDueDate = dialogView.findViewById<EditText>(R.id.editDueDate)
        val editTextLocation = dialogView.findViewById<EditText>(R.id.editLocation)
        val spinnerPriority = dialogView.findViewById<Spinner>(R.id.spinnerPriority)
        val buttonAddTask = dialogView.findViewById<Button>(R.id.addTaskButton)

        // Set up the Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPriority.adapter = adapter
        }

        val dialog = builder.create()

        // Set focus on the input field
        editTextTask.requestFocus()

        // Show keyboard automatically
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        buttonAddTask.setOnClickListener {
            val taskName = editTextTask.text.toString().trim()
            val taskDescription = editTextTaskDescription.text.toString().trim()
            val dueDate = editTextDueDate.text.toString().trim()
            val location = editTextLocation.text.toString().trim()
            val priority = spinnerPriority.selectedItem.toString()

            if (taskName.isNotEmpty() && dueDate.isNotEmpty()) {
                // Convert dueDate string to Date object
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dueDateObj: Date? = try {
                    dateFormat.parse(dueDate)
                } catch (e: Exception) {
                    null
                }

                if (dueDateObj != null) {
                    // Add task to Firestore
                    addTaskToFirestore(taskName, taskDescription, priority, dueDateObj, location)
                    // Dismiss the dialog
                    dialog.dismiss()
                } else {
                    editTextDueDate.error = "Invalid date format"
                }
            } else {
                if (taskName.isEmpty()) {
                    editTextTask.error = "Task name cannot be empty"
                }
                if (dueDate.isEmpty()) {
                    editTextDueDate.error = "Due date cannot be empty"
                }
            }
        }

        dialog.show()
    }

    private fun addTaskToFirestore(taskName: String, taskDescription: String, priority: String, dueDate: Date, location: String) {
        // Create a new task object with default values for taskStatus and userId
        val task = TaskModel().apply {
            this.taskName = taskName
            this.taskStatus = "Pending"
            this.userId = auth.currentUser?.email.toString()
            this.priority = priority
            this.dueDate = dueDate.toString()
            this.location = location
            this.taskDescription = taskDescription
        }

        // Add task to Firestore
        firestore.collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                // Task added successfully
                // You can perform any additional actions here if needed
            }
            .addOnFailureListener { e ->
                // Error occurred while adding task
                // Handle the error gracefully
            }
    }
}
