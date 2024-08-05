package com.example.taskpal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HelpCenterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_center)

        val back = findViewById<Button>(R.id.Back)
        back.setOnClickListener{
            startActivity(Intent(this, AccountSetting::class.java))
        }
    }
}