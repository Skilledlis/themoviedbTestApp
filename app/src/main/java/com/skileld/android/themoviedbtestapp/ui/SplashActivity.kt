package com.skileld.android.themoviedbtestapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.skileld.android.themoviedbtestapp.R

class SplashActivity : AppCompatActivity() {

    private lateinit var splashLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashLayout = findViewById(R.id.splash_activity)
        splashLayout.alpha = 0f
        splashLayout.animate().setDuration(200).alpha(1f).withEndAction {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}