package com.andranikas.videoroom.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.andranikas.videoroom.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, VideoRoomFragment.newInstance(), TAG)
        }
    }

    companion object {
        private const val TAG = "video"
    }
}
