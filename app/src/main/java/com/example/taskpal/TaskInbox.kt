package com.example.taskpal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpal.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskInbox : AppCompatActivity() {
    private var taskRv: RecyclerView? = null
    private val dataList = ArrayList<TaskModel>()
    private var taskListAdapter: TaskListAdapter? = null
    private var db: FirebaseFirestore? = null
    private val TAG = "TaskInbox"

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_inbox)
        supportActionBar?.hide()

        val imageViewHome = findViewById<ImageView>(R.id.home)
//        val imageViewInbox = findViewById<ImageView>(R.id.inbox)
        val imageViewCalendarView = findViewById<ImageView>(R.id.CalendarView)
        val imageViewFilterViews = findViewById<ImageView>(R.id.FilterViews)
        val imageViewProject = findViewById<ImageView>(R.id.project)
        val goBack = findViewById<Button>(R.id.button6)


        imageViewHome.setOnClickListener {
            startActivity(Intent(this@TaskInbox, HomeActivity::class.java))
        }

//        imageViewInbox.setOnClickListener {
//            startActivity(Intent(this@TaskInbox, TaskInbox::class.java))
//        }

        imageViewCalendarView.setOnClickListener {
            startActivity(Intent(this@TaskInbox, CalendarViewActivity::class.java))
        }

        imageViewFilterViews.setOnClickListener {
            startActivity(Intent(this@TaskInbox, FilterViewsActivity::class.java))
        }

        imageViewProject.setOnClickListener {
            startActivity(Intent(this@TaskInbox, ProjectActivity::class.java))
        }

        goBack.setOnClickListener{
            finish()
        }


        db = FirebaseFirestore.getInstance()
        taskRv = findViewById(R.id.taskListRv)
        taskListAdapter = TaskListAdapter(dataList)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        taskRv?.layoutManager = layoutManager
        taskRv?.adapter = taskListAdapter

        findViewById<View>(R.id.addTaskFAB).setOnClickListener {
            startActivity(Intent(this@TaskInbox, HomeActivity::class.java))
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db!!.collection("tasks")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val taskModel = document.toObject(TaskModel::class.java)
                            taskModel.taskId = document.id
                            dataList.add(taskModel)
                        }
                        taskListAdapter?.notifyDataSetChanged()
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
        }

        // Search functionality
        val searchView = findViewById<SearchView>(R.id.searchview)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                Log.d("search", " $s")
                taskListAdapter?.clearAllItems()
                db!!.collection("tasks")
                    .orderBy("taskName")
                    .startAt(s).endAt(s + '\uf8ff')
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                val taskModel = document.toObject(TaskModel::class.java)
                                taskModel.taskId = document.id
                                dataList.add(taskModel)
                            }
                            taskListAdapter?.notifyDataSetChanged()
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.exception)
                        }
                    }
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
    }

}
