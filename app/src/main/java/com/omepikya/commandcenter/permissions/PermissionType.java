package com.omepikya.commandcenter.permissions;

public enum PermissionType {

    MICROPHONE(
            "android.permission.RECORD_AUDIO"
    ),

    CAMERA(
            "android.permission.CAMERA"
    ),

    PHONE(
            "android.permission.CALL_PHONE"
    ),

    CONTACTS(
            "android.permission.READ_CONTACTS"
    ),

    SMS(
            "android.permission.SEND_SMS"
    ),

    LOCATION(
            "android.permission.ACCESS_FINE_LOCATION"
    ),

    NOTIFICATIONS(
            "android.permission.POST_NOTIFICATIONS"
    );

    private final String permission;

    PermissionType(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}