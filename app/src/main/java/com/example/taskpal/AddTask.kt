package com.example.taskpal

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddTask : AppCompatActivity() {

    private val TAG = "AddTask"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_layout)

        supportActionBar?.title = "Add Task"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val saveBtn = findViewById<Button>(R.id.taskSaveBtn)
        val etTaskInput = findViewById<EditText>(R.id.inputTaskName)
        val db = Firebase.firestore

        saveBtn.setOnClickListener {
            val taskName = etTaskInput.text.toString().trim()
            if (taskName.isNotEmpty()) {
                val taskModel = TaskModel("", taskName, "PENDING", FirebaseAuth.getInstance().uid)
                db.collection("tasks").add(taskModel)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        // Show a success message or update UI if needed
                        etTaskInput.setText("") // Clear the input field after adding task
                        // Optionally, you can navigate back to the previous screen or show a toast
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error adding document", e)
                        // Handle failure, show error message to the user if needed
                    }
            } else {
                etTaskInput.error = "Task name cannot be empty"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == androidx.appcompat.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
