package com.example.taskpal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class FilterViewsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_views)

        val imageViewHome = findViewById<ImageView>(R.id.home)
        val imageViewInbox = findViewById<ImageView>(R.id.inbox)
        val imageViewCalendarView = findViewById<ImageView>(R.id.CalendarView)
//        val imageViewFilterViews = findViewById<ImageView>(R.id.FilterViews)
        val imageViewProject = findViewById<ImageView>(R.id.project)
        val goBack = findViewById<Button>(R.id.button6)


        imageViewHome.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, HomeActivity::class.java))
        }

        imageViewInbox.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, TaskInbox::class.java))
        }

        imageViewCalendarView.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, CalendarViewActivity::class.java))
        }

//        imageViewFilterViews.setOnClickListener {
//            startActivity(Intent(this@FilterViewsActivity, FilterViewsActivity::class.java))
//        }

        imageViewProject.setOnClickListener {
            startActivity(Intent(this@FilterViewsActivity, ProjectActivity::class.java))
        }

        goBack.setOnClickListener{
            finish()
        }
    }
}