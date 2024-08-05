package com.example.taskpal

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("taskName")
        val taskId = intent.getStringExtra("taskId")

        val builder = NotificationCompat.Builder(context, "taskChannel")
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle("Task Reminder")
            .setContentText("Reminder for task: $taskName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(taskId.hashCode(), builder.build())
            }
        } else {
            // Handle the case where permission is not granted
            // You may log this case or notify the user through another method
        }
    }
}
