package com.example.taskpal

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Toast
import android.widget.EditText
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var Btn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val Signup: Button = findViewById(R.id.register)

        Signup.setOnClickListener {
            Intent(this@LoginActivity, RegistrationActivity::class.java).also { startActivity(it) }
        }
        val reset: Button = findViewById(R.id.reset)

        reset.setOnClickListener {
            Intent(this@LoginActivity, ForgotPassword::class.java).also { startActivity(it) }
        }

        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email)
        passwordTextView = findViewById(R.id.password)
        Btn = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar)

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener {
            loginUserAccount()
        }

    }

    private fun loginUserAccount() {

        // show the visibility of progress bar to show loading
        progressBar.visibility = View.VISIBLE

        // Take the value of two edit texts in Strings
        val email: String = emailTextView.text.toString()
        val password: String = passwordTextView.text.toString()

        // validations for input email and password
        if (email.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter email!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter password!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Login successful!!",
                        Toast.LENGTH_LONG
                    ).show()

                    // hide the progress bar
                    progressBar.visibility = View.GONE

                    // if sign-in is successful
                    // intent to home activity
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {

                    // sign-in failed
                    Toast.makeText(
                        applicationContext,
                        "Login failed!!",
                        Toast.LENGTH_LONG
                    ).show()

                    // hide the progress bar
                    progressBar.visibility = View.GONE
                }
            }
    }
}
