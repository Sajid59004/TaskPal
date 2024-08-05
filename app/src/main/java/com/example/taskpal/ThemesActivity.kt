package com.example.taskpal
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*

class ThemesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        // Set onClickListeners for theme cards
        findViewById<MaterialCardView>(R.id.materialCardView1).setOnClickListener {
            // Set theme and save preference
            setThemeAndSaveToFirebase(R.color.colorAccent1)
        }

        // Repeat the above block for each new theme card
    }

    private fun setThemeAndSaveToFirebase(themeColorResId: Int) {
        // Set the selected theme color
        setTheme(themeColorResId)

        // Save the selected theme color to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("themeColor", themeColorResId)
        editor.apply()

        // Update the theme preference in Firebase for the user
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            val userRef = database.child("users").child(uid)
            userRef.child("themeColor").setValue(themeColorResId)
                .addOnSuccessListener {
                    Toast.makeText(this, "Theme updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to update theme: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
