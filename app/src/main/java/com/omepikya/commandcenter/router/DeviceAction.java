package com.omepikya.commandcenter.router;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.provider.Settings;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

public class DeviceAction implements Action {

    private final Context context;

    public DeviceAction(Context context) {

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
        return "Device Action";
    }

    @Override
    public boolean canHandle(
            CommandContext context
    ) {

        return context != null
                && context.getCommandType()
                == CommandType.DEVICE_ACTION;
    }

    @Override
    public CommandResult execute(
            CommandContext commandContext
    ) {

        String command =
                commandContext.getRawCommand()
                        .toLowerCase();

        if (command.contains("battery")) {

            BatteryManager batteryManager =
                    (BatteryManager) context
                            .getSystemService(
                                    Context.BATTERY_SERVICE
                            );

            if (batteryManager == null) {

                return CommandResult.failure(
                        "Battery information is unavailable."
                );
            }

            int level =
                    batteryManager.getIntProperty(
                            BatteryManager.BATTERY_PROPERTY_CAPACITY
                    );

            if (level >= 0) {

                return CommandResult.success(
                        "Battery level is "
                                + level
                                + " percent."
                );
            }

            try {

                Intent intent =
                        new Intent(
                                Settings.ACTION_BATTERY_SAVER_SETTINGS
                        );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                );

                context.startActivity(intent);

                return CommandResult.success(
                        "Opening battery settings"
                );

            } catch (Exception e) {

                return CommandResult.failure(
                        "Battery information is unavailable."
                );
            }
        }

        return CommandResult.failure(
                "I don't know that device action yet."
        );
    }
}