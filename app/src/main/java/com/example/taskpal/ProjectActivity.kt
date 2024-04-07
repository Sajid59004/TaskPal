package com.example.taskpal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class ProjectActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        val imageViewHome = findViewById<ImageView>(R.id.home)
        val imageViewInbox = findViewById<ImageView>(R.id.inbox)
        val imageViewCalendarView = findViewById<ImageView>(R.id.CalendarView)
        val imageViewFilterViews = findViewById<ImageView>(R.id.FilterViews)
//        val imageViewProject = findViewById<ImageView>(R.id.project)
        val goBack = findViewById<Button>(R.id.button6)


        imageViewHome.setOnClickListener {
            startActivity(Intent(this@ProjectActivity, HomeActivity::class.java))
        }

        imageViewInbox.setOnClickListener {
            startActivity(Intent(this@ProjectActivity, TaskInbox::class.java))
        }

        imageViewCalendarView.setOnClickListener {
            startActivity(Intent(this@ProjectActivity, CalendarViewActivity::class.java))
        }

        imageViewFilterViews.setOnClickListener {
            startActivity(Intent(this@ProjectActivity, FilterViewsActivity::class.java))
        }

//        imageViewProject.setOnClickListener {
//            startActivity(Intent(this@ProjectActivity, ProjectActivity::class.java))
//        }

        goBack.setOnClickListener{
            finish()
        }
    }
}