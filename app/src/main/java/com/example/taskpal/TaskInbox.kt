package com.example.taskpal

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpal.adapter.TaskAdapter
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date


class TaskInbox : AppCompatActivity() {

    private val TAG = "TaskInboxActivity"
    private lateinit var taskAdapter: TaskAdapter
    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_inbox)

        supportActionBar?.title = "Task Inbox"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(mutableListOf(), this)
        recyclerView.adapter = taskAdapter

        checkAndRequestNotificationPermission()
        loadTasks()
    }

    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
            } else {
                // Permission denied
                // Handle the case where the user denied the permission
            }
        }
    }

    private fun loadTasks() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.email ?: return

        val db = Firebase.firestore
        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = mutableListOf<TaskModel>()
                for (document in documents) {
                    val task = document.toObject(TaskModel::class.java)
                    tasks.add(task)
                }
                taskAdapter.setTasks(tasks)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting documents", e)
                Toast.makeText(this, "Error loading tasks", Toast.LENGTH_SHORT).show()
            }
    }


    fun setAlarmForTask(context: Context, task: TaskModel) {
        // Parse the due date string to Date object
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dueDate: Date? = dateFormat.parse(task.dueDate)

        if (dueDate != null) {
            // Calculate the alarm time (10 minutes before due date)
            val calendar = Calendar.getInstance().apply {
                time = dueDate
                add(Calendar.MINUTE, -10)
            }

            // Create an intent to trigger the alarm receiver
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("taskName", task.taskName)
                putExtra("taskId", task.id)
            }

            // Create a PendingIntent for the alarm
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Get the AlarmManager service
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Set the alarm
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            // Show a toast message to indicate that the alarm is set
            Toast.makeText(context, "Alarm set for task: ${task.taskName}", Toast.LENGTH_SHORT).show()
        } else {
            // Handle the case where the due date string is not in the expected format
            // Notify the user or log an error message
            Toast.makeText(context, "Error setting alarm for task: ${task.taskName}", Toast.LENGTH_SHORT).show()
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
