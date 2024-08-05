package com.example.taskpal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpal.model.TaskModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class TaskListAdapter(private val taskDataset: ArrayList<TaskModel>) :
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskNameTv: TextView
        val taskStatusTv: TextView
//        var containerLl: LinearLayout

        init {
            // Define click listener for the ViewHolder's View
            taskNameTv = view.findViewById<View>(R.id.taskNameTv) as TextView
            taskStatusTv = view.findViewById<View>(R.id.taskStatusTv) as TextView
//            containerLl = view.findViewById<View>(R.id.containerLL) as LinearLayout
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_task, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.taskNameTv.text = taskDataset[position].taskName
        viewHolder.taskStatusTv.text = taskDataset[position].taskStatus
        val status = taskDataset[position].taskStatus
        if (status!!.lowercase(Locale.getDefault()) == "pending") {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFFF00"))
        } else if (status.lowercase(Locale.getDefault()) == "completed") {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#00FF00"))
        } else {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#ffffff"))
        }
//        viewHolder.containerLl.setOnLongClickListener { view ->
//            val popupMenu = PopupMenu(view.context, viewHolder.containerLl)
//            popupMenu.inflate(R.menu.taskmenu)
//            popupMenu.show()
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                if (menuItem.itemId == R.id.deleteMenu) {
//                    taskDataset[position].taskId?.let {
//                        FirebaseFirestore.getInstance().collection("tasks")
//                            .document(it).delete()
//                            .addOnSuccessListener(OnSuccessListener<Void?> {
//                                Toast.makeText(view.context, "Item deleted", Toast.LENGTH_SHORT)
//                                    .show()
//                                viewHolder.containerLl.visibility = View.GONE
//                            })
//                    }
//                }
//                if (menuItem.itemId == R.id.markCompleteMenu) {
//                    val completedTask = taskDataset[position]
//                    completedTask.taskStatus = "completed"
//                    taskDataset[position].taskId?.let {
//                        FirebaseFirestore.getInstance().collection("tasks")
//                            .document(it)
//                            .set(completedTask).addOnSuccessListener(OnSuccessListener<Void?> {
//                                Toast.makeText(
//                                    view.context,
//                                    "Task Item Marked As Completed",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            })
//                    }
//                    viewHolder.taskStatusTv.setBackgroundColor(
//                        Color.parseColor(
//                            "#00FF00"
//                        )
//                    )
//                    viewHolder.taskStatusTv.text = "COMPLETED"
//                }
//                false
//            }
//            false
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return taskDataset.size
    }

    fun clearAllItems() {
        taskDataset.clear()
        notifyDataSetChanged()
    }

}