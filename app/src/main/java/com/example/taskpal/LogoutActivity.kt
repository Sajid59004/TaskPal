package com.example.taskpal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LogoutActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)


        val textView = findViewById<TextView>(R.id.nameTextField)
        val signOutButton = findViewById<Button>(R.id.logoutButton)
        val back = findViewById<Button>(R.id.back)

        back.setOnClickListener{
            startActivity(Intent(this, AccountSetting::class.java))
        }

        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Initialize Google SignIn options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if user is signed in and update UI
        val currentUser = mAuth.currentUser
        currentUser?.let {
            textView.text = "Hello, ${it.displayName}"
        }

    }
    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this, AccountCreate::class.java)
            startActivity(intent)
            finish()
        }
    }
}
