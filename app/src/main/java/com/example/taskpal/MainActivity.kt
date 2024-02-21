package com.example.taskpal

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpal.R.id.gfg

class MainActivity : AppCompatActivity() {

    private lateinit var geeksforgeeks: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextView
        geeksforgeeks = findViewById(R.id.gfg) ?: return // Perform null check

        // Set text for TextView
        geeksforgeeks.text = "GeeksForGeeks(Firebase Authentication)"
    }
}
