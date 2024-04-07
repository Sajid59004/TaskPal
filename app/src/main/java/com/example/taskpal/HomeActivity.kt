package com.example.taskpal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextClock
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpal.model.TaskModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        val textAlarmTime: TextClock = findViewById(R.id.textClockDate)
        val materialCardView = findViewById<MaterialCardView>(R.id.materialCardView)
        val imageViewSettings = findViewById<ImageView>(R.id.settings)
//        val imageViewHome = findViewById<ImageView>(R.id.home)
        val imageViewInbox = findViewById<ImageView>(R.id.inbox)
        val imageViewCalendarView = findViewById<ImageView>(R.id.CalendarView)
        val imageViewFilterViews = findViewById<ImageView>(R.id.FilterViews)
        val imageViewProject = findViewById<ImageView>(R.id.project)

        // Set onClickListeners
        textAlarmTime.setOnClickListener {
            showTimePickerDialog()
        }

        materialCardView.setOnClickListener {
            showAddTaskDialog()
        }

        imageViewSettings.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AccountSetting::class.java))
        }

//        imageViewHome.setOnClickListener {
//            startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
//        }

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

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Handle time selection
                // You can implement any logic here if needed
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.popup_layout, null)
        builder.setView(dialogView)

        val editTextTask = dialogView.findViewById<EditText>(R.id.inputTaskName)
        val buttonAddTask = dialogView.findViewById<Button>(R.id.taskSaveBtn)

        val dialog = builder.create()

        // Set focus on the input field
        editTextTask.requestFocus()

        // Show keyboard automatically
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        buttonAddTask.setOnClickListener {
            val taskName = editTextTask.text.toString().trim()
            if (taskName.isNotEmpty()) {
                // Add task to Firestore
                addTaskToFirestore(taskName)
                // Dismiss the dialog
                dialog.dismiss()
            } else {
                // Show an error message if task name is empty
                editTextTask.error = "Task name cannot be empty"
            }
        }

        dialog.show()
    }

    private fun addTaskToFirestore(taskName: String) {
        // Create a new task object with default values for taskStatus and userId
        val task = TaskModel().apply {
            this.taskName = taskName
            this.taskStatus = "Pending"
            this.userId = auth.currentUser?.uid
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
