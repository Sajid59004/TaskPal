package com.example.taskpal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Toast
import android.widget.EditText
import android.widget.Button
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp

class RegistrationActivity : AppCompatActivity() {

    private lateinit var emailTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var Btn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val Login: Button = findViewById(R.id.login)

        Login.setOnClickListener {
            Intent(this@RegistrationActivity, LoginActivity::class.java).also { startActivity(it) }
        }

        // Initialize FirebaseApp
        FirebaseApp.initializeApp(this)

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        // Initialize views
        emailTextView = findViewById(R.id.email)
        passwordTextView = findViewById(R.id.passwd)
        Btn = findViewById(R.id.btnregister)
        progressBar = findViewById(R.id.progressbar)

        // Set onClickListener for the registration button
        Btn.setOnClickListener {
            registerNewUser()
        }
    }

    private fun registerNewUser() {
        progressBar.visibility = View.VISIBLE

        val email: String = emailTextView.text.toString()
        val password: String = passwordTextView.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(applicationContext, "Please enter email and password!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                    val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
            })
    }
}
