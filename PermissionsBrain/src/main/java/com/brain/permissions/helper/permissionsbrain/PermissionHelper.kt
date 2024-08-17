package com.brain.permissions.helper.permissionsbrain

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.io.Serializable

/**
 * <pre>
 * Created on 17/08/2024
 * </pre>
 *
 * @author Zeeshan
 */

object PermissionHelper {
    var isLoggingEnabled: Boolean = true

    /**
     * Disable logging.
     */
    fun disableLogging() {
        isLoggingEnabled = false
    }

    internal fun log(message: String) {
        if (isLoggingEnabled) android.util.Log.d("PermissionHelper", message)
    }

    /**
     * Check/request a permission and call the appropriate callback methods of PermissionManager.
     *
     * @param context The Android context.
     * @param permission The permission to be requested.
     * @param rationale Explanation to be shown to the user if they have denied permission earlier.
     * @param handler The PermissionManager instance for handling callbacks.
     */
    fun checkPermission(
        context: Context,
        permission: String,
        rationale: String?,
        handler: PermissionCallback
    ) {
        checkPermissions(context, arrayOf(permission), rationale, null, handler)
    }

    /**
     * Check/request a permission and call the appropriate callback methods of PermissionManager.
     *
     * @param context The Android context.
     * @param permission The permission to be requested.
     * @param rationaleId The string resource ID of the explanation to be shown to the user if they have denied permission earlier.
     * @param handler The PermissionManager instance for handling callbacks.
     */
    fun checkPermission(
        context: Context,
        permission: String,
        @StringRes rationaleId: Int,
        handler: PermissionCallback
    ) {
        val rationale: String? = try {
            context.getString(rationaleId)
        } catch (e: Exception) {
            null
        }
        checkPermissions(context, arrayOf(permission), rationale, null, handler)
    }

    /**
     * Check/request permissions and call the appropriate callback methods of PermissionManager.
     *
     * @param context The Android context.
     * @param permissions The array of permissions to request.
     * @param rationale Explanation to be shown to the user if they have denied permissions earlier.
     * @param options Options for handling permissions.
     * @param handler The PermissionManager instance for handling callbacks.
     */
    fun checkPermissions(
        context: Context,
        permissions: Array<String>,
        rationale: String?,
        options: PermissionOptions?,
        handler: PermissionCallback
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            handler.onPermissionsGranted()
            log("Android version < 23")
        } else {
            val permissionsSet = permissions.toSet()
            val allPermissionsGranted = permissionsSet.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }

            if (allPermissionsGranted) {
                handler.onPermissionsGranted()
                log("Permissions ${if (PermissionActivity.permissionHandler == null) "already granted." else "just granted from settings."}")
                PermissionActivity.permissionHandler = null
            } else {
                PermissionActivity.permissionHandler = handler
                val permissionsList = ArrayList(permissionsSet)

                val intent = Intent(context, PermissionActivity::class.java).apply {
                    putStringArrayListExtra(PermissionActivity.EXTRA_PERMISSIONS, permissionsList)
                    putExtra(PermissionActivity.EXTRA_RATIONALE, rationale)
                    putExtra(PermissionActivity.EXTRA_OPTIONS, options)
                    if (options?.createNewTask == true) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                context.startActivity(intent)
            }
        }
    }

    /**
     * Check/request permissions and call the appropriate callback methods of PermissionManager.
     *
     * @param context The Android context.
     * @param permissions The array of permissions to request.
     * @param rationaleId The string resource ID of the explanation to be shown to the user if they have denied permissions earlier.
     * @param options Options for handling permissions.
     * @param handler The PermissionManager instance for handling callbacks.
     */
    fun checkPermissions(
        context: Context,
        permissions: Array<String>,
        @StringRes rationaleId: Int,
        options: PermissionOptions?,
        handler: PermissionCallback
    ) {
        val rationale: String? = try {
            context.getString(rationaleId)
        } catch (e: Exception) {
            null
        }
        checkPermissions(context, permissions, rationale, options, handler)
    }

    /**
     * Options to customize when requesting permissions.
     */
    class PermissionOptions : Serializable {
        var settingsButtonText: String = "Settings"
        var rationaleDialogTitle: String = "Permissions Required"
        var settingsDialogTitle: String = "Permissions Required"
        var settingsDialogMessage: String =
            "Required permission(s) have been set not to ask again! Please provide them from settings."
        var sendBlockedToSettings: Boolean = true
        var createNewTask: Boolean = false

        /**
         * Set the button text for "Settings" when asking the user to go to settings.
         */
        fun setSettingsButtonText(text: String): PermissionOptions {
            settingsButtonText = text
            return this
        }

        /**
         * Set the "Create new Task" flag in Intent, for when this library is called from a Service or other non-activity context.
         */
        fun setCreateNewTask(flag: Boolean): PermissionOptions {
            createNewTask = flag
            return this
        }

        /**
         * Set the title text for the permission rationale dialog.
         */
        fun setRationaleDialogTitle(title: String): PermissionOptions {
            rationaleDialogTitle = title
            return this
        }

        /**
         * Set the title text of the dialog that asks the user to go to settings when permissions have been set not to ask again.
         */
        fun setSettingsDialogTitle(title: String): PermissionOptions {
            settingsDialogTitle = title
            return this
        }

        /**
         * Set the message of the dialog that asks the user to go to settings when permissions have been set not to ask again.
         */
        fun setSettingsDialogMessage(message: String): PermissionOptions {
            settingsDialogMessage = message
            return this
        }

        /**
         * Determine whether to ask the user to go to settings if permissions have been set not to ask again.
         */
        fun sendToSettings(send: Boolean): PermissionOptions {
            sendBlockedToSettings = send
            return this
        }
    }
}