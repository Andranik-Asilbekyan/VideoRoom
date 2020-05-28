package com.andranikas.videoroom.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.andranikas.videoroom.helpers.navigateToLanding

class SplashActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            navigateToLanding(this)
            finish()
        }, 500)
    }
}