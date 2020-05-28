package com.andranikas.videoroom.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.andranikas.videoroom.R

class PermissionsFragment : DialogFragment() {

    private var listener: OnPermissionFragmentInteractionListener? = null
    private lateinit var permissions: Array<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPermissionFragmentInteractionListener) {
            listener = context
        } else if (parentFragment is OnPermissionFragmentInteractionListener) {
            listener = parentFragment as OnPermissionFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissions = arguments?.getStringArray(ARG_PERMISSIONS) ?: throw IllegalArgumentException("Getting permissions failed")
        if (!hasPermissions(requireContext(), permissions)) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        } else {
            listener?.onPermissionsGranted()
            dismiss()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            checkPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (hasPermissions(requireContext(), permissions)) {
            listener?.onPermissionsGranted()
            dismiss()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), alreadyDeniedPermission())) {
                listener?.onPermissionsDenied()
                dismiss()
            } else {
                showPermissionInfoDialog()
            }
        }
    }

    private fun alreadyDeniedPermission(): String =
        permissions.first {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_DENIED
        }

    private fun showPermissionInfoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_denied_title)
            .setMessage(R.string.permission_denied_message)
            .setNegativeButton(R.string.permission_close_button) { _, _ -> activity?.finish() }
            .setPositiveButton(R.string.permission_settings_button) { _, _ -> openDetailSettingsForResult() }
            .show()
    }

    private fun openDetailSettingsForResult() {
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context?.packageName, null)
        }.run {
            startActivityForResult(this, PERMISSIONS_REQUEST_CODE)
        }
    }

    interface OnPermissionFragmentInteractionListener {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 101
        private const val ARG_PERMISSIONS = "permissions"

        fun hasPermissions(context: Context, permissions: Array<String>) = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        fun newInstance(permissions: Array<String>) =
            PermissionsFragment().apply {
                arguments = Bundle().apply {
                    putStringArray(ARG_PERMISSIONS, permissions)
                }
            }
    }
}
