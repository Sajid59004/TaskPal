package com.example.taskpal.adapter

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpal.AlarmReceiver
import com.example.taskpal.R
import com.example.taskpal.model.TaskModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(private var taskDataset: MutableList<TaskModel> = mutableListOf(), private val context: Context) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskNameTv: TextView = view.findViewById(R.id.taskNameTv)
        val taskDescriptionTv: TextView = view.findViewById(R.id.taskDescriptionTv)
        val taskStatusTv: TextView = view.findViewById(R.id.taskStatusTv)
        val taskPriorityTv: TextView = view.findViewById(R.id.taskPriorityTv)
        val taskDueDateTv: TextView = view.findViewById(R.id.taskDueDateTv)
        val taskLocationTv: TextView = view.findViewById(R.id.taskLocationTv)
        val editButton: Button = view.findViewById(R.id.editButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val doneButton: Button = view.findViewById(R.id.doneButton)
        val alarmButton: Button = view.findViewById(R.id.alarmButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_task, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val task = taskDataset[position]
        viewHolder.taskNameTv.text = task.taskName
        viewHolder.taskDescriptionTv.text = task.taskDescription
        viewHolder.taskPriorityTv.text = task.priority
        viewHolder.taskDueDateTv.text = task.dueDate
        viewHolder.taskLocationTv.text = task.location
        viewHolder.taskStatusTv.text = task.taskStatus
        val status = task.taskStatus
        if (status!!.lowercase(Locale.getDefault()) == "pending") {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFFF00"))
        } else if (status.lowercase(Locale.getDefault()) == "completed") {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#00FF00"))
        } else {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        viewHolder.doneButton.setOnClickListener {
            updateTaskStatus(task, "COMPLETED")
        }

        viewHolder.editButton.setOnClickListener {
            showEditTaskDialog(task)
        }

        viewHolder.deleteButton.setOnClickListener {
            deleteTask(task)
        }

        viewHolder.alarmButton.setOnClickListener {
            setAlarmForTask(task)
        }
    }

    override fun getItemCount(): Int {
        return taskDataset.size
    }

    fun setTasks(taskList: List<TaskModel>) {
        this.taskDataset.clear()
        this.taskDataset.addAll(taskList)
        notifyDataSetChanged()
    }

    private fun updateTaskStatus(task: TaskModel, status: String) {
        val db = Firebase.firestore
        db.collection("tasks")
            .document(task.id)
            .update("taskStatus", status)
            .addOnSuccessListener {
                Toast.makeText(context, "Task status updated to $status", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error updating task status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTask(task: TaskModel) {
        val db = Firebase.firestore
        db.collection("tasks")
            .document(task.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                taskDataset.remove(task)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error deleting task", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditTaskDialog(task: TaskModel) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_layout, null)

        val taskNameEditText: EditText = popupView.findViewById(R.id.editTaskName)
        val taskDescriptionEditText: EditText = popupView.findViewById(R.id.editTaskDescription)
        val prioritySpinner: Spinner = popupView.findViewById(R.id.spinnerPriority)
        val dueDateEditText: EditText = popupView.findViewById(R.id.editDueDate)
        val locationEditText: EditText = popupView.findViewById(R.id.editLocation)
        val addTaskButton: Button = popupView.findViewById(R.id.addTaskButton)

        taskNameEditText.setText(task.taskName)
        taskDescriptionEditText.setText(task.taskDescription)
        // Set the spinner to the correct priority value
        val priorityArray = context.resources.getStringArray(R.array.priority_array)
        val priorityIndex = priorityArray.indexOf(task.priority)
        if (priorityIndex >= 0) {
            prioritySpinner.setSelection(priorityIndex)
        }
        dueDateEditText.setText(task.dueDate)
        locationEditText.setText(task.location)

        val dialog = AlertDialog.Builder(context)
            .setView(popupView)
            .create()

        addTaskButton.setText("Update Task")
        addTaskButton.setOnClickListener {
            val updatedTaskName = taskNameEditText.text.toString().trim()
            val updatedTaskDescription = taskDescriptionEditText.text.toString().trim()
            val updatedPriority = prioritySpinner.selectedItem.toString().trim()
            val updatedDueDate = dueDateEditText.text.toString().trim()
            val updatedLocation = locationEditText.text.toString().trim()

            if (updatedTaskName.isEmpty() || updatedDueDate.isEmpty()) {
                Toast.makeText(context, "Task Name and Due Date are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTask = task.copy(
                taskName = updatedTaskName,
                taskDescription = updatedTaskDescription,
                priority = updatedPriority,
                dueDate = updatedDueDate,
                location = updatedLocation
            )

            val db = Firebase.firestore
            db.collection("tasks")
                .document(updatedTask.id)
                .set(updatedTask)
                .addOnSuccessListener {
                    Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                    setTasks(taskDataset.map { if (it.id == updatedTask.id) updatedTask else it })
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating task", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    private fun setAlarmForTask(task: TaskModel) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dueDate = sdf.parse(task.dueDate) // Convert string to Date object

        val calendar = Calendar.getInstance().apply {
            time = dueDate
            add(Calendar.MINUTE, -10) // set alarm 10 minutes before due date
        }


    val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("taskName", task.taskName)
            putExtra("taskId", task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, task.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(context, "Alarm set for task: ${task.taskName}", Toast.LENGTH_SHORT).show()
    }

}
