package com.example.taskpal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccountCreate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_layout)

        val skip: Button = findViewById(R.id.mail)

        skip.setOnClickListener {
            Intent(this@AccountCreate, LoginActivity::class.java).also { startActivity(it) }
        }

    }
}
