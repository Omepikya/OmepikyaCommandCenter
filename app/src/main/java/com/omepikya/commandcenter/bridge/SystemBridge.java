package com.omepikya.commandcenter.bridge;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;
import java.util.Locale;

public class SystemBridge {

    private final Context context;
    private final PackageManager packageManager;

    public SystemBridge(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();

        this.packageManager =
                this.context.getPackageManager();
    }

    /**
     * Opens an installed application using
     * its Android package name.
     */
    public boolean openApp(String packageName) {

        if (packageName == null
                || packageName.trim().isEmpty()) {

            return false;
        }

        Intent launchIntent =
                packageManager.getLaunchIntentForPackage(
                        packageName.trim()
                );

        if (launchIntent == null) {
            return false;
        }

        launchIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        try {

            context.startActivity(
                    launchIntent
            );

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Finds an installed launchable application
     * by its visible application name.
     */
    public String findAppPackage(
            String appName
    ) {

        if (appName == null
                || appName.trim().isEmpty()) {

            return null;
        }

        String target =
                normalize(appName);

        List<ApplicationInfo> applications =
                packageManager
                        .getInstalledApplications(
                                PackageManager.GET_META_DATA
                        );

        String partialMatch = null;

        for (ApplicationInfo application :
                applications) {

            String packageName =
                    application.packageName;

            Intent launchIntent =
                    packageManager
                            .getLaunchIntentForPackage(
                                    packageName
                            );

            /*
             * Ignore applications that Android
             * cannot launch directly.
             */
            if (launchIntent == null) {
                continue;
            }

            CharSequence label =
                    application.loadLabel(
                            packageManager
                    );

            if (label == null) {
                continue;
            }

            String appLabel =
                    normalize(
                            label.toString()
                    );

            /*
             * Exact match gets priority.
             */
            if (appLabel.equals(target)) {
                return packageName;
            }

            /*
             * Keep a possible partial match.
             */
            if (!appLabel.isEmpty()
                    && (appLabel.contains(target)
                    || target.contains(appLabel))) {

                if (partialMatch == null) {
                    partialMatch = packageName;
                }
            }
        }

        return partialMatch;
    }

    /**
     * Returns a list of launchable applications.
     */
    public List<ApplicationInfo> getInstalledApps() {

        return packageManager
                .getInstalledApplications(
                        PackageManager.GET_META_DATA
                );
    }

    /**
     * Opens Android's application settings
     * for a specific package.
     */
    public void openAppSettings(
            String packageName
    ) {

        if (packageName == null
                || packageName.trim().isEmpty()) {

            return;
        }

        Intent intent =
                new Intent(
                        Settings
                                .ACTION_APPLICATION_DETAILS_SETTINGS
                );

        intent.setData(
                Uri.parse(
                        "package:"
                                + packageName.trim()
                )
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        try {

            context.startActivity(
                    intent
            );

        } catch (Exception ignored) {
            // Settings activity unavailable.
        }
    }

    /**
     * Opens the main Android Settings screen.
     */
    public void openSystemSettings() {

        Intent intent =
                new Intent(
                        Settings.ACTION_SETTINGS
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        try {

            context.startActivity(
                    intent
            );

        } catch (Exception ignored) {
            // Settings activity unavailable.
        }
    }

    /**
     * Returns the application context used
     * by this bridge.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Normalizes application names so matching
     * is case-insensitive and whitespace-safe.
     */
    private String normalize(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .toLowerCase(Locale.US)
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }
}