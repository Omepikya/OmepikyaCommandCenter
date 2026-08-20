package com.omepikya.commandcenter.permissions;

import android.app.Activity;
import android.content.Context;

import java.util.EnumSet;
import java.util.Set;

public final class PermissionIntelligence {

    private final PermissionManager manager;

    public PermissionIntelligence(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        manager = new PermissionManager(context);
    }

    /**
     * Checks whether a permission is currently granted.
     */
    public boolean isGranted(PermissionType type) {

        if (type == null) {
            return false;
        }

        return manager.isGranted(type);
    }

    /**
     * Requests a permission from Android.
     *
     * @return true if permission was already granted,
     *         false if a request was initiated or could
     *         not be initiated.
     */
    public boolean request(
            Activity activity,
            PermissionType type) {

        if (activity == null || type == null) {
            return false;
        }

        if (manager.isGranted(type)) {
            return true;
        }

        manager.requestPermission(
                activity,
                type
        );

        return false;
    }

    /**
     * Checks whether Android recommends explaining
     * the permission before requesting it again.
     */
    public boolean shouldExplain(
            Activity activity,
            PermissionType type) {

        if (activity == null || type == null) {
            return false;
        }

        return manager.shouldShowRationale(
                activity,
                type
        );
    }

    /**
     * Returns all permissions currently missing.
     */
    public Set<PermissionType> missing() {

        Set<PermissionType> result =
                EnumSet.noneOf(PermissionType.class);

        for (PermissionType type :
                PermissionType.values()) {

            if (!manager.isGranted(type)) {
                result.add(type);
            }
        }

        return result;
    }

    /**
     * Returns the underlying PermissionManager.
     */
    public PermissionManager getManager() {
        return manager;
    }
}