package com.omepikya.commandcenter.models;

public class AppInfo {

    private final String appName;
    private final String packageName;
    private final boolean launchable;

    public AppInfo(
            String appName,
            String packageName,
            boolean launchable
    ) {
        this.appName = appName;
        this.packageName = packageName;
        this.launchable = launchable;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isLaunchable() {
        return launchable;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", launchable=" + launchable +
                '}';
    }
}