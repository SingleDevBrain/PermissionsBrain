package com.brain.permissions.helper.permissionsbrain

import android.content.Context

/**
 * <pre>
 * Callback on user action
 * Created on 17/08/2024
 * </pre>
 *
 * @author Zeeshan
 */

abstract class PermissionCallback {
    /**
     * Called when all requested permissions are granted.
     */
    abstract fun onPermissionsGranted()

    /**
     * Called when some of the requested permissions are denied.
     *
     * @param context The application context.
     * @param deniedPermissions The list of permissions that were denied.
     */
    open fun onPermissionsDenied(context: Context, deniedPermissions: List<String>) {
        if (PermissionHelper.isLoggingEnabled) {
            val deniedList = deniedPermissions.joinToString(separator = " ") { it }
            PermissionHelper.log("Denied: $deniedList")
        }

    }

    /**
     * Called when some permissions have been previously set to not ask again.
     *
     * @param context The application context.
     * @param blockedPermissions The list of permissions set to not ask again.
     * @return Return true if no further action is needed, false if the default action (sending the user to settings) should be taken.
     */
    open fun onPermissionsBlocked(context: Context, blockedPermissions: List<String>): Boolean {
        if (PermissionHelper.isLoggingEnabled) {
            val blockedList = blockedPermissions.joinToString(separator = " ") { it }
            PermissionHelper.log("Set not to ask again: $blockedList")
        }
        return false
    }

    /**
     * Called when some permissions have just been set to not ask again.
     *
     * @param context The application context.
     * @param justBlockedPermissions The list of permissions just set to not ask again.
     * @param deniedPermissions The list of currently denied permissions.
     */
    open fun onPermissionsJustBlocked(context: Context, justBlockedPermissions: List<String>, deniedPermissions: List<String>) {
        if (PermissionHelper.isLoggingEnabled) {
            val justBlockedList = justBlockedPermissions.joinToString(separator = " ") { it }
            PermissionHelper.log("Just set not to ask again: $justBlockedList")
        }
        onPermissionsDenied(context, deniedPermissions)
    }
}
