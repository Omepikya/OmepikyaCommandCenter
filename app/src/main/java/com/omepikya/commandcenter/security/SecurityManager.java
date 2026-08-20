package com.omepikya.commandcenter.security;

import android.content.Context;

import com.omepikya.commandcenter.permissions.PermissionType;

public class SecurityManager {

    private final SecureStorage secureStorage;

    public SecurityManager(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        secureStorage =
                new SecureStorage(context);
    }

    public boolean isValidCommand(String command) {

        if (command == null) {
            return false;
        }

        String normalized = command.trim();

        if (normalized.isEmpty()) {
            return false;
        }

        if (normalized.length() > 500) {
            return false;
        }

        return true;
    }

    public boolean requiresPermission(
            PermissionType permissionType
    ) {

        return permissionType != null;
    }

    public SecureStorage getSecureStorage() {
        return secureStorage;
    }
}