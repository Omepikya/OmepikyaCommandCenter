package com.omepikya.commandcenter.security;

import android.app.Activity;

import com.omepikya.commandcenter.permissions.PermissionManager;
import com.omepikya.commandcenter.permissions.PermissionType;

public class PermissionGuard {

    private final PermissionManager permissionManager;

    public PermissionGuard(Activity activity) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity cannot be null"
            );
        }

        permissionManager =
                new PermissionManager(activity);
    }

    public boolean hasPermission(
            PermissionType permissionType
    ) {

        return permissionManager.isGranted(
                permissionType
        );
    }

    public boolean requirePermission(
            Activity activity,
            PermissionType permissionType
    ) {

        if (permissionType == null) {
            return false;
        }

        if (hasPermission(permissionType)) {
            return true;
        }

        permissionManager.requestPermission(
                activity,
                permissionType
        );

        return false;
    }

    public boolean shouldShowRationale(
            Activity activity,
            PermissionType permissionType
    ) {

        return permissionManager.shouldShowRationale(
                activity,
                permissionType
        );
    }
}