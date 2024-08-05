package com.example.taskpal

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class AccountSetting : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_settings)

        val EditProfile = findViewById<Button>(R.id.button9)
        EditProfile.setOnClickListener {
            Intent(this@AccountSetting, EditProfileActivity::class.java).also { startActivity(it) }
        }

        val helpCenter = findViewById<Button>(R.id.button15)
        helpCenter.setOnClickListener {
            Intent(this@AccountSetting, HelpCenterActivity::class.java).also { startActivity(it) }
        }

        val themes = findViewById<Button>(R.id.button10)
        themes.setOnClickListener {
            Intent(this@AccountSetting, ThemesActivity::class.java).also { startActivity(it) }
        }

        val policy = findViewById<Button>(R.id.button14)
        policy.setOnClickListener {
            Intent(this@AccountSetting, PrivacyPolicy::class.java).also { startActivity(it) }
        }

        val userEmailTextView: TextView = findViewById(R.id.textView16)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Initialize Google SignIn options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = getClient(this, gso)

        // Initialize views
        val myButton = findViewById<Button>(R.id.button6)
        val textView = findViewById<TextView>(R.id.name)
        val signOutButton = findViewById<Button>(R.id.button16)

        // Set OnClickListener for home button
        myButton.setOnClickListener {
            // Replace HomeActivity::class.java with the desired activity
            Intent(this@AccountSetting, HomeActivity::class.java).also { startActivity(it) }
        }

        // Check if user is signed in and update UI
        val currentUser = mAuth.currentUser
        currentUser?.let {
            textView.text = "Welcome, ${it.displayName}"
            // Set user email
        }

        currentUser?.let { user ->
            // Set user email
            userEmailTextView.text = user.email
        }

        // Set OnClickListener for sign out button
        signOutButton.setOnClickListener {
            Intent(this@AccountSetting, LogoutActivity::class.java).also { startActivity(it) }
        }
    }
}

