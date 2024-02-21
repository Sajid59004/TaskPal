package com.example.taskpal

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mytext: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextView
        mytext = findViewById(R.id.mytext) ?: return // Perform null check

        // Set text for TextView
        mytext.text = "Home Page"
    }
}
