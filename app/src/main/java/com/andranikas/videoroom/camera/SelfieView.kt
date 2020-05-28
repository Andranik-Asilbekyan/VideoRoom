package com.andranikas.videoroom.camera

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.andranikas.videoroom.R
import com.andranikas.videoroom.camera.core.CameraProvider
import com.andranikas.videoroom.helpers.showPermissionSnackBar
import com.andranikas.videoroom.permission.PermissionsFragment
import kotlinx.android.synthetic.main.selfie_view.view.*

class SelfieView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var childFragmentManager: FragmentManager
    private lateinit var lifecycleOwner: LifecycleOwner
    private var resolution: Int = 0

    init {
        inflate(context, R.layout.selfie_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!PermissionsFragment.hasPermissions(context, CAMERA_PERMISSIONS)) {
            showPermissionFragment()
        } else {
            startCamera()
        }
    }

    fun setup(resolution: Int = 0, childFragmentManager: FragmentManager, lifecycleOwner: LifecycleOwner) {
        this.resolution = resolution
        this.childFragmentManager = childFragmentManager
        this.lifecycleOwner = lifecycleOwner
    }

    fun onPermissionsGranted() {
        startCamera()
    }

    fun onPermissionsDenied() {
        showPermissionSnackBar(this) {
            showPermissionFragment()
        }
    }

    private fun showPermissionFragment() {
        PermissionsFragment.newInstance(CAMERA_PERMISSIONS).showNow(childFragmentManager, TAG)
    }

    private fun startCamera() {
        CameraProvider(resolution, lifecycleOwner, preview_view).run {
            startCamera()
        }
    }

    companion object {
        private val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "Permissions"
    }
}