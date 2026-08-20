package com.omepikya.commandcenter.router;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

public class SystemSettingsAction implements Action {

    private static final String ACTION_NOTIFICATION_SETTINGS =
            "android.settings.NOTIFICATION_SETTINGS";

    private final Context context;

    public SystemSettingsAction(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();
    }

    @Override
    public String getName() {
        return "System Settings";
    }

    @Override
    public boolean canHandle(
            CommandContext context
    ) {

        if (context == null) {
            return false;
        }

        return context.getCommandType()
                == CommandType.SYSTEM_SETTING;
    }

    @Override
    public CommandResult execute(
            CommandContext context
    ) {

        if (context == null) {

            return CommandResult.failure(
                    "Command context is null"
            );
        }

        String command =
                context.getRawCommand();

        if (command == null
                || command.trim().isEmpty()) {

            return CommandResult.failure(
                    "No settings command was specified"
            );
        }

        String normalized =
                command
                        .toLowerCase()
                        .trim();

        try {

            if (containsAny(
                    normalized,
                    "wifi",
                    "wi-fi"
            )) {

                openSettings(
                        Settings.ACTION_WIFI_SETTINGS
                );

                return CommandResult.success(
                        "Opening Wi-Fi settings"
                );
            }

            if (containsAny(
                    normalized,
                    "bluetooth"
            )) {

                openSettings(
                        Settings.ACTION_BLUETOOTH_SETTINGS
                );

                return CommandResult.success(
                        "Opening Bluetooth settings"
                );
            }

            if (containsAny(
                    normalized,
                    "brightness",
                    "display"
            )) {

                openSettings(
                        Settings.ACTION_DISPLAY_SETTINGS
                );

                return CommandResult.success(
                        "Opening display settings"
                );
            }

            if (containsAny(
                    normalized,
                    "volume",
                    "sound",
                    "audio"
            )) {

                openSettings(
                        Settings.ACTION_SOUND_SETTINGS
                );

                return CommandResult.success(
                        "Opening sound settings"
                );
            }

            if (containsAny(
                    normalized,
                    "airplane",
                    "airplane mode"
            )) {

                openSettings(
                        Settings.ACTION_AIRPLANE_MODE_SETTINGS
                );

                return CommandResult.success(
                        "Opening airplane mode settings"
                );
            }

            if (containsAny(
                    normalized,
                    "mobile data",
                    "mobile network",
                    "cellular"
            )) {

                openSettings(
                        Settings.ACTION_WIRELESS_SETTINGS
                );

                return CommandResult.success(
                        "Opening wireless settings"
                );
            }

            if (containsAny(
                    normalized,
                    "location",
                    "gps"
            )) {

                openSettings(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                );

                return CommandResult.success(
                        "Opening location settings"
                );
            }

            if (containsAny(
                    normalized,
                    "notification",
                    "notifications"
            )) {

                openSettings(
                        ACTION_NOTIFICATION_SETTINGS
                );

                return CommandResult.success(
                        "Opening notification settings"
                );
            }

            openSettings(
                    Settings.ACTION_SETTINGS
            );

            return CommandResult.success(
                    "Opening system settings"
            );

        } catch (Exception e) {

            return CommandResult.failure(
                    "Unable to open system settings"
            );
        }
    }

    private void openSettings(
            String action
    ) {

        Intent intent =
                new Intent(action);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        context.startActivity(intent);
    }

    private boolean containsAny(
            String text,
            String... values
    ) {

        for (String value : values) {

            if (text.contains(value)) {
                return true;
            }
        }

        return false;
    }
}