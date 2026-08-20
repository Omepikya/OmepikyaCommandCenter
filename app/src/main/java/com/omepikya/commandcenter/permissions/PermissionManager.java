package com.omepikya.commandcenter.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    private static final int DEFAULT_REQUEST_CODE = 1001;

    private final Context context;

    public PermissionManager(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context = context;
    }

    public boolean isGranted(PermissionType type) {

        if (type == null) {
            return false;
        }

        return ContextCompat.checkSelfPermission(
                context,
                type.getPermission()
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isDenied(PermissionType type) {

        return !isGranted(type);
    }

    public void requestPermission(
            Activity activity,
            PermissionType type
    ) {

        if (activity == null || type == null) {
            return;
        }

        if (isGranted(type)) {
            return;
        }

        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        type.getPermission()
                },
                DEFAULT_REQUEST_CODE
        );
    }

    public boolean shouldShowRationale(
            Activity activity,
            PermissionType type
    ) {

        if (activity == null || type == null) {
            return false;
        }

        return ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                type.getPermission()
        );
    }

    public static String[] getAllPermissions() {

        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }
}