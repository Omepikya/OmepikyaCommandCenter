package com.omepikya.commandcenter.router;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

public class MediaAction implements Action {

    private final AudioManager audioManager;

    public MediaAction(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        audioManager =
                (AudioManager) context
                        .getApplicationContext()
                        .getSystemService(
                                Context.AUDIO_SERVICE
                        );
    }

    @Override
    public String getName() {
        return "Media";
    }

    @Override
    public boolean canHandle(
            CommandContext context
    ) {

        return context != null
                && context.getCommandType()
                == CommandType.MEDIA;
    }

    @Override
    public CommandResult execute(
            CommandContext context
    ) {

        if (audioManager == null) {

            return CommandResult.failure(
                    "Media control is unavailable."
            );
        }

        String command =
                context.getRawCommand()
                        .toLowerCase();

        int keyCode;

        if (command.contains("next")
                || command.contains("skip")) {

            keyCode =
                    KeyEvent.KEYCODE_MEDIA_NEXT;

        } else if (command.contains("previous")
                || command.contains("back")) {

            keyCode =
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS;

        } else if (command.contains("pause")) {

            keyCode =
                    KeyEvent.KEYCODE_MEDIA_PAUSE;

        } else if (command.contains("resume")
                || command.contains("play")) {

            keyCode =
                    KeyEvent.KEYCODE_MEDIA_PLAY;

        } else {

            keyCode =
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
        }

        long now =
                System.currentTimeMillis();

        audioManager.dispatchMediaKeyEvent(
                new KeyEvent(
                        now,
                        now,
                        KeyEvent.ACTION_DOWN,
                        keyCode,
                        0
                )
        );

        audioManager.dispatchMediaKeyEvent(
                new KeyEvent(
                        now,
                        now,
                        KeyEvent.ACTION_UP,
                        keyCode,
                        0
                )
        );

        return CommandResult.success(
                mediaMessage(keyCode)
        );
    }

    private String mediaMessage(
            int keyCode
    ) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_MEDIA_NEXT:
                return "Next track";

            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                return "Previous track";

            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                return "Pausing media";

            case KeyEvent.KEYCODE_MEDIA_PLAY:
                return "Playing media";

            default:
                return "Toggling media playback";
        }
    }
}