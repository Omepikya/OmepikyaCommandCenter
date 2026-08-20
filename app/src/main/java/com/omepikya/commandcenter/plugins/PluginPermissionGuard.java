package com.omepikya.commandcenter.plugins;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.PluginPermission;

public final class PluginPermissionGuard {

    private final Context context;

    public PluginPermissionGuard(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();
    }

    public boolean canExecute(
            DeveloperPlugin plugin
    ) {

        return getMissingPermission(
                plugin
        ) == null;
    }

    public String getMissingPermission(
            DeveloperPlugin plugin
    ) {

        if (plugin == null) {
            return "PLUGIN";
        }

        for (
                PluginPermission permission :
                plugin.getRequiredPermissions()
        ) {

            if (permission == null
                    || permission ==
                    PluginPermission.NONE
                    || permission ==
                    PluginPermission.INTERNET) {

                continue;
            }

            if (!isGranted(permission)) {
                return permission.name();
            }
        }

        return null;
    }

    private boolean isGranted(
            PluginPermission permission
    ) {

        String manifestPermission;

        switch (permission) {

            case CONTACTS:
                manifestPermission =
                        Manifest.permission.READ_CONTACTS;
                break;

            case PHONE:
                manifestPermission =
                        Manifest.permission.CALL_PHONE;
                break;

            case SMS:
                manifestPermission =
                        Manifest.permission.SEND_SMS;
                break;

            case LOCATION:
                manifestPermission =
                        Manifest.permission.ACCESS_FINE_LOCATION;
                break;

            case MICROPHONE:
                manifestPermission =
                        Manifest.permission.RECORD_AUDIO;
                break;

            case NOTIFICATIONS:

                if (android.os.Build.VERSION.SDK_INT < 33) {
                    return true;
                }

                manifestPermission =
                        "android.permission.POST_NOTIFICATIONS";
                break;

            default:
                return true;
        }

        return context.checkSelfPermission(
                manifestPermission
        ) == PackageManager.PERMISSION_GRANTED;
    }
}