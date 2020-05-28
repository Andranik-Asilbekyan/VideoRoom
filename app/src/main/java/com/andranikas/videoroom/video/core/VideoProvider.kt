package com.andranikas.videoroom.video.core

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class VideoProvider(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val view: PlayerView
): LifecycleObserver {

    private var player: SimpleExoPlayer? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun startVideo() {
        player = SimpleExoPlayer.Builder(context).build()
        view.player = player
        player?.prepare(createMediaSource())
        player?.playWhenReady = true
    }

    private fun reStartVideo() {
        player?.playWhenReady = true
    }

    private fun pauseVideo() {
        player?.playWhenReady = false
    }

    private fun createMediaSource(): MediaSource =
        LoopingMediaSource(
            ProgressiveMediaSource
            .Factory(DefaultHttpDataSourceFactory(Util.getUserAgent(context, APP_NAME)))
            .createMediaSource(Uri.parse(VIDEO_PATH))
        )

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        reStartVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        pauseVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lifecycleOwner.lifecycle.removeObserver(this)
        player?.release()
    }

    companion object {
        private const val APP_NAME = "TenVideo"
        private const val VIDEO_PATH = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }
}