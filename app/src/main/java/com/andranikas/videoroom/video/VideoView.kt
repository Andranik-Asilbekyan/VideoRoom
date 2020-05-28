package com.andranikas.videoroom.video

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.andranikas.videoroom.R
import com.andranikas.videoroom.video.core.VideoProvider
import kotlinx.android.synthetic.main.video_view.view.*

class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var lifecycleOwner: LifecycleOwner

    init {
        inflate(context, R.layout.video_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startVideo()
    }

    fun setup(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    private fun startVideo() {
        VideoProvider(context, lifecycleOwner, player_view).run {
            startVideo()
        }
    }
}