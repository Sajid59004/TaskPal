package com.example.taskpal

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class ChooseTheme : AppCompatActivity() {

    private var selectedColor: Int = R.color.colorAccent1 // Default color
    private lateinit var openTaskPal: Button
    private lateinit var cardViews: List<MaterialCardView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_theme)

        // Initialize views
        openTaskPal = findViewById(R.id.openTaskPal)
        cardViews = listOf(
            findViewById(R.id.materialCardView),
            findViewById(R.id.materialCardView2),
            findViewById(R.id.materialCardView3),
            findViewById(R.id.materialCardView4)
        )

        // Set click listeners for card views
        cardViews.forEach { cardView ->
            cardView.setOnClickListener {
                handleCardClick(cardView)
            }
        }

        // Set initial selected card view
        handleCardClick(cardViews.first())

        // Set click listener for openTaskPal button
        openTaskPal.setOnClickListener {
            openTaskPal(it)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun handleCardClick(cardView: MaterialCardView) {
        // Remove bold border from previously selected card view
        cardViews.forEach { it.strokeWidth = 0 }

        // Set bold border for clicked card view
        cardView.strokeWidth = 4

        // Update selected color
        selectedColor = cardView.cardBackgroundColor.defaultColor

        // Update button color
        openTaskPal.setBackgroundColor(selectedColor)
    }

    fun openTaskPal(view: View) {
        // Set the theme and start HomeActivity
        val intent = Intent(this@ChooseTheme, HomeActivity::class.java)
        intent.putExtra("themeColor", selectedColor)
        startActivity(intent)
        finish()
    }
}
