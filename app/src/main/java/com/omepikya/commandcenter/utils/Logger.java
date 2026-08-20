package com.omepikya.commandcenter.utils;

import android.util.Log;

import com.omepikya.commandcenter.config.Constants;

/**
 * Central application logger.
 *
 * Security rules:
 *
 * DEBUG / INFO:
 *     Enabled only in debug builds.
 *
 * WARNING / ERROR:
 *     Allowed in release builds for operational diagnostics,
 *     but callers must not pass raw user data, commands,
 *     contact information, tokens, credentials, or automation
 *     contents.
 */
public final class Logger {

    private Logger() {
        // Utility class.
    }

    /**
     * Debug-only diagnostic logging.
     *
     * Never reaches logcat in release builds.
     */
    public static void debug(String message) {

        if (!isDebugBuild()
                || message == null
                || message.isEmpty()) {
            return;
        }

        Log.d(
                Constants.TAG,
                message
        );
    }

    /**
     * Debug-only informational logging.
     *
     * Never reaches logcat in release builds.
     */
    public static void info(String message) {

        if (!isDebugBuild()
                || message == null
                || message.isEmpty()) {
            return;
        }

        Log.i(
                Constants.TAG,
                message
        );
    }

    /**
     * Warning logging.
     *
     * Do NOT pass user-controlled or sensitive data.
     */
    public static void warning(String message) {

        if (message == null
                || message.isEmpty()) {
            return;
        }

        Log.w(
                Constants.TAG,
                sanitize(message)
        );
    }

    /**
     * Error logging.
     *
     * Do NOT pass user-controlled or sensitive data.
     */
    public static void error(String message) {

        if (message == null
                || message.isEmpty()) {
            return;
        }

        Log.e(
                Constants.TAG,
                sanitize(message)
        );
    }

    /**
     * Error logging with exception.
     *
     * The exception itself is useful for diagnostics, but callers
     * must ensure the exception message does not contain sensitive
     * application data.
     */
    public static void error(
            String message,
            Throwable throwable
    ) {

        if (message == null
                || message.isEmpty()) {
            return;
        }

        if (throwable == null) {

            Log.e(
                    Constants.TAG,
                    sanitize(message)
            );

            return;
        }

        Log.e(
                Constants.TAG,
                sanitize(message),
                throwable
        );
    }

    /**
     * Returns whether this is a debug build.
     *
     * Kept in one place so the release logging policy cannot
     * accidentally become inconsistent between methods.
     */
    private static boolean isDebugBuild() {

        return com.omepikya.commandcenter.BuildConfig.DEBUG;
    }

    /**
     * Removes obvious control characters from log messages.
     *
     * This is not intended to make arbitrary user data safe.
     * Callers must still avoid logging sensitive/user-controlled
     * content in warning/error logs.
     */
    private static String sanitize(
            String message
    ) {

        if (message == null) {
            return "";
        }

        return message
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ')
                .trim();
    }
}