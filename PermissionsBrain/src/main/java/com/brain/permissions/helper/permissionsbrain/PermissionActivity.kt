package com.brain.permissions.helper.permissionsbrain

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * <pre>
 * Activity to handle Permissions
 * Created on 17/08/2024
 * </pre>
 *
 * @author Zeeshan
 */

class PermissionActivity : Activity() {
    companion object {
        const val RC_SETTINGS = 6739
        const val RC_PERMISSION = 6937

        const val EXTRA_PERMISSIONS = "permissions"
        const val EXTRA_RATIONALE = "rationale"
        const val EXTRA_OPTIONS = "options"

        var permissionHandler: PermissionCallback? = null
    }

    private lateinit var allPermissions: ArrayList<String>
    private lateinit var deniedPermissions: ArrayList<String>
    private lateinit var noRationalePermissions: ArrayList<String>
    private var options: PermissionHelper.PermissionOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(false)

        val intent = intent ?: run {
            finish()
            return
        }

        allPermissions = intent.getStringArrayListExtra(EXTRA_PERMISSIONS) ?: run {
            finish()
            return
        }

        options = intent.getSerializableExtra(EXTRA_OPTIONS) as? PermissionHelper.PermissionOptions
            ?: PermissionHelper.PermissionOptions()

        deniedPermissions = ArrayList()
        noRationalePermissions = ArrayList()

        val permissionsToRequest = allPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            permissionHandler?.onPermissionsGranted()
            finish()
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                RC_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_PERMISSION) {
            deniedPermissions.clear()
            noRationalePermissions.clear()

            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission)
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        noRationalePermissions.add(permission)
                    }
                }
            }

            when {
                deniedPermissions.isEmpty() -> {
                    permissionHandler?.onPermissionsGranted()
                    finish()
                }

                noRationalePermissions.isNotEmpty() -> {
                    showBlockedDialog()
                }

                else -> {
                    showRationaleDialog()
                }
            }
        }
    }

    private fun showRationaleDialog() {
        val rationale = intent.getStringExtra(EXTRA_RATIONALE)
        if (!rationale.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle(options?.rationaleDialogTitle)
                .setMessage(rationale)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        deniedPermissions.toTypedArray(),
                        RC_PERMISSION
                    )
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    permissionHandler?.onPermissionsDenied(this, deniedPermissions)
                    finish()
                }
                .show()
        } else {
            permissionHandler?.onPermissionsDenied(this, deniedPermissions)
            finish()
        }
    }

    private fun showBlockedDialog() {
        if (options?.sendBlockedToSettings == true) {
            AlertDialog.Builder(this)
                .setTitle(options?.settingsDialogTitle)
                .setMessage(options?.settingsDialogMessage)
                .setCancelable(false)
                .setPositiveButton(options?.settingsButtonText) { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivityForResult(intent, RC_SETTINGS)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    permissionHandler?.onPermissionsJustBlocked(
                        this,
                        noRationalePermissions,
                        deniedPermissions
                    )
                    finish()
                }
                .show()
        } else {
            permissionHandler?.onPermissionsJustBlocked(
                this,
                noRationalePermissions,
                deniedPermissions
            )
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SETTINGS) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), RC_PERMISSION)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        permissionHandler = null
        super.onDestroy()
    }
}