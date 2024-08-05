package com.example.taskpal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val titleTextView: TextView = findViewById(R.id.heading)
        val privacyPolicyTextView: TextView = findViewById(R.id.privacyPolicyTextView)

        // Set title text
        titleTextView.text = getString(R.string.privacy_policy_title)

        // Set privacy policy content
//        privacyPolicyTextView.text = getString(R.string.privacy_policy_content)

        val Back = findViewById<Button>(R.id.button6)
        Back.setOnClickListener{
            startActivity(Intent(this@PrivacyPolicy, AccountSetting::class.java))
        }
    }
}