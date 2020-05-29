package com.andranikas.videoroom.camera.core

import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraProvider(
    private val resolution: Int = 0,
    private val lifecycleOwner: LifecycleOwner,
    private val view: PreviewView
): CameraXConfig.Provider {

    override fun getCameraXConfig(): CameraXConfig =
        Camera2Config.defaultConfig()

    fun startCamera() {
        view.post {
            bindCameraUseCases()
        }
    }

    private fun bindCameraUseCases() {
        val rotation = view.display.rotation

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(view.context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                when (resolution) {
                    0 -> {
                        val metrics = DisplayMetrics().also { view.display.getRealMetrics(it) }
                        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
                        setTargetAspectRatio(screenAspectRatio)
                    }
                    else -> setTargetResolution(Size(resolution, resolution))
                }
            }.setTargetRotation(rotation).build()

            try {
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                preview.setSurfaceProvider(view.createSurfaceProvider())
            } catch(exc: Exception) {
                Log.e(LOG_TAG, "Camera preview failed", exc)
            }
        }, ContextCompat.getMainExecutor(view.context))
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    companion object {
        private const val LOG_TAG = "Camera"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}
