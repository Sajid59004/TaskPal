package com.example.taskpal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var taskId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val editTextTask = findViewById<EditText>(R.id.editTaskName)
        val editTextTaskDescription = findViewById<EditText>(R.id.editTaskDescription)
        val editTextDueDate = findViewById<EditText>(R.id.editDueDate)
        val editTextLocation = findViewById<EditText>(R.id.editLocation)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        val buttonUpdateTask = findViewById<Button>(R.id.updateTaskButton)

        // Set up the Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPriority.adapter = adapter
        }

        // Get taskId from intent
        taskId = intent.getStringExtra("taskId") ?: ""

        // Load task data
        loadTaskData(taskId, editTextTask, editTextTaskDescription, editTextDueDate, editTextLocation, spinnerPriority)

        buttonUpdateTask.setOnClickListener {
            val taskName = editTextTask.text.toString().trim()
            val taskDescription = editTextTaskDescription.text.toString().trim()
            val dueDate = editTextDueDate.text.toString().trim()
            val location = editTextLocation.text.toString().trim()
            val priority = spinnerPriority.selectedItem.toString()

            if (taskName.isNotEmpty() && dueDate.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dueDateObj: Date? = try {
                    dateFormat.parse(dueDate)
                } catch (e: Exception) {
                    null
                }

                if (dueDateObj != null) {
                    // Update task in Firestore
                    updateTaskInFirestore(taskName, taskDescription, priority, dueDateObj, location)
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
    }

    private fun loadTaskData(taskId: String, taskName: EditText, taskDescription: EditText, dueDate: EditText, location: EditText, priority: Spinner) {
        firestore.collection("tasks").document(taskId).get().addOnSuccessListener { document ->
            if (document != null) {
                taskName.setText(document.getString("taskName"))
                taskDescription.setText(document.getString("taskDescription"))
                dueDate.setText(document.getString("dueDate"))
                location.setText(document.getString("location"))
                val priorityValue = document.getString("priority")
                val priorityArray = resources.getStringArray(R.array.priority_array)
                val priorityPosition = priorityArray.indexOf(priorityValue)
                priority.setSelection(priorityPosition)
            }
        }
    }

    private fun updateTaskInFirestore(taskName: String, taskDescription: String, priority: String, dueDate: Date, location: String) {
        val task = TaskModel().apply {
            this.taskName = taskName
            this.taskStatus = "Pending"
            this.userId = auth.currentUser?.email.toString()
            this.priority = priority
            this.dueDate = dueDate.toString()
            this.location = location
            this.taskDescription = taskDescription
        }

        firestore.collection("tasks").document(taskId)
            .set(task)
            .addOnSuccessListener {
                // Task updated successfully
                finish()
            }
            .addOnFailureListener { e ->
                // Error occurred while updating task
                // Handle the error gracefully
            }
    }
}
