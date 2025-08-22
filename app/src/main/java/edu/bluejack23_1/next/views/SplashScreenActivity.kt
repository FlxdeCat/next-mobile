package edu.bluejack23_1.next.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.views.auth.LoginActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val backgroundImage : ImageView = findViewById(R.id.imageViewLogoSplash)
        val fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade)
        val logoTV : TextView = findViewById(R.id.splashLogoTV)
        backgroundImage.startAnimation(fadeAnimation)
        logoTV.startAnimation(fadeAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}