package com.andranikas.videoroom.helpers

import android.content.Context
import android.content.Intent
import android.view.View
import com.andranikas.videoroom.R
import com.andranikas.videoroom.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

fun showNoSpaceAlertDialog(context: Context) {
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.no_space_dialog_title)
        .setMessage(R.string.no_space_dialog_message)
        .setNegativeButton(android.R.string.cancel, null)
        .create()
        .show()
}

fun showPermissionSnackBar(view: View, action: () -> Unit) {
    Snackbar.make(view, R.string.permission_no_camera_permission, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.permission_request_button) { action.invoke() }
        .show()
}

fun navigateToLanding(context: Context) {
    context.startActivity(Intent(context, MainActivity::class.java))
}