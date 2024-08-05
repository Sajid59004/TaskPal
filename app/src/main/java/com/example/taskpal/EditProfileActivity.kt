package com.example.taskpal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var profileImageView: ImageView
    private lateinit var userNameEditText: EditText

    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            profileImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        val userEmailTextView: TextView = findViewById(R.id.emailTextView)
        userNameEditText = findViewById(R.id.nameEditText)
        val saveChangesButton: Button = findViewById(R.id.saveButton)
        profileImageView = findViewById(R.id.profileImageView)
        val changeImageButton: Button = findViewById(R.id.changeImageButton)

        val storage = Firebase.storage
        val storageRef = storage.reference

        currentUser?.let { user ->
            // Set user email
            userEmailTextView.text = user.email

            // Fetch and set current user name
            fetchUserName(user.uid) { userName ->
                userName?.let {
                    userNameEditText.setText(it)
                } ?: run {
                    userNameEditText.setText("Username") // Default username
                }
            }

            // Set click listener on profile image to open image picker
            profileImageView.setOnClickListener {
                openImagePicker()
            }

            // Set click listener on change image button
            changeImageButton.setOnClickListener {
                openImagePicker()
            }

            // Set click listener on save button
            saveChangesButton.setOnClickListener {
                val newName = userNameEditText.text.toString().trim()
                updateProfile(user, newName)
            }
        }
    }

    private fun fetchUserName(uid: String?, callback: (String?) -> Unit) {
        uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("name")
                    callback(userName)
                }
                .addOnFailureListener { e ->
                    // Handle error fetching user name
                    showToast("Failed to fetch user name")
                    callback(null)
                }
        }
    }

    private fun updateProfile(currentUser: FirebaseUser?, newName: String) {
        currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        selectedImageUri?.let { uri ->
                            updateProfileImage(user, uri)
                        } ?: run {
                            redirectToAccountSettingPage("Name updated successfully")
                        }
                    } else {
                        showToast("Failed to update profile")
                    }
                }
        }
    }

    private fun updateProfileImage(currentUser: FirebaseUser, imageUri: Uri) {
        // Upload image to Firebase Storage
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${currentUser.uid}/profile.jpg")

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // Image uploaded successfully, get download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                // Update profile image URL in Firestore
                firestore.collection("users").document(currentUser.uid)
                    .update("profileImage", downloadUrl)
                    .addOnSuccessListener {
                        redirectToAccountSettingPage("Profile updated successfully")
                    }
                    .addOnFailureListener { e ->
                        showToast("Failed to update profile image")
                    }
            }
        }.addOnFailureListener { e ->
            showToast("Failed to upload image")
        }
    }

    private fun openImagePicker() {
        getContent.launch("image/*")
    }

    private fun redirectToAccountSettingPage(message: String) {
        showToast(message)
        val intent = Intent(this, AccountSetting::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
