package com.brain.premissions.helper.brainpremissonshelper

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.brain.permissions.helper.permissionsbrain.PermissionCallback
import com.brain.permissions.helper.permissionsbrain.PermissionHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btn_ask_all).setOnClickListener {
            val permissions =
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                } else {
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                    )
                }

            val rationale = "Please provide access to work properly"
            val permissionOption: PermissionHelper.PermissionOptions = PermissionHelper.PermissionOptions()
                .setRationaleDialogTitle("Permissions Required...!")
                .setSettingsDialogTitle("Urgent")
            PermissionHelper.checkPermissions(
                this,
                permissions,
                rationale,
                permissionOption,
                object : PermissionCallback() {
                    override fun onPermissionsGranted() {
                        Toast.makeText(this@MainActivity, "All permissions granted", Toast.LENGTH_SHORT).show()

                    }

                    override fun onPermissionsDenied(
                        context: Context,
                        deniedPermissions: List<String>
                    ) {
                        super.onPermissionsDenied(context, deniedPermissions)
                        Toast.makeText(this@MainActivity, "All permissions denied", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        findViewById<Button>(R.id.btn_camera).setOnClickListener {
            PermissionHelper.checkPermission(
                this,
                Manifest.permission.CAMERA,
                "Camera Permissions Required....!",
                object : PermissionCallback() {
                    override fun onPermissionsGranted() {
                        Toast.makeText(
                            this@MainActivity,
                            "Camera permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionsDenied(
                        context: Context,
                        deniedPermissions: List<String>
                    ) {
                        super.onPermissionsDenied(context, deniedPermissions)
                        Toast.makeText(
                            this@MainActivity,
                            "Camera permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }

        findViewById<Button>(R.id.btn_media).setOnClickListener {
            val premissions = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            PermissionHelper.checkPermissions(
                this,
                premissions,
                "Media Permissions required...!",
                null,
                object : PermissionCallback() {
                    override fun onPermissionsGranted() {
                        Toast.makeText(
                            this@MainActivity,
                            "Media permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionsDenied(
                        context: Context,
                        deniedPermissions: List<String>
                    ) {
                        super.onPermissionsDenied(context, deniedPermissions)
                        Toast.makeText(
                            this@MainActivity,
                            "Media permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }
    }
}