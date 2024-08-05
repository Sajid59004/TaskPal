package com.example.taskpal

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpal.R
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTask : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_layout)

        showAddTaskPopup()
    }

    private fun showAddTaskPopup() {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_layout, null)

        val taskNameEditText: EditText = popupView.findViewById(R.id.editTaskName)
        val taskDescriptionEditText: EditText = popupView.findViewById(R.id.editTaskDescription)
        val priorityEditText: EditText = popupView.findViewById(R.id.spinnerPriority)
        val dueDateEditText: EditText = popupView.findViewById(R.id.editDueDate)
        val locationEditText: EditText = popupView.findViewById(R.id.editLocation)
        val addTaskButton: Button = popupView.findViewById(R.id.addTaskButton)

        val dialog = AlertDialog.Builder(this)
            .setView(popupView)
            .create()

        addTaskButton.setOnClickListener {
            val taskName = taskNameEditText.text.toString().trim()
            val taskDescription = taskDescriptionEditText.text.toString().trim()
            val priority = priorityEditText.text.toString().trim()
            val dueDate = dueDateEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()

            if (taskName.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Task Name and Due Date are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.email ?: return@setOnClickListener

            val selectedCalendar = Calendar.getInstance()
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedCalendar.time = sdf.parse(dueDate) ?: Date()
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taskModel = TaskModel(
                id = UUID.randomUUID().toString(),
                taskName = taskName,
                taskStatus = "PENDING",
                userId = userId,
                priority = priority,
                dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.time),
                location = location,
                taskDescription = taskDescription
            )

            val db = Firebase.firestore
            db.collection("tasks")
                .document(taskModel.id)
                .set(taskModel)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
}
