package com.omepikya.commandcenter.voice;

public class SpeechResult {

    private final boolean success;
    private final String text;
    private final String errorMessage;

    private SpeechResult(
            boolean success,
            String text,
            String errorMessage
    ) {
        this.success = success;
        this.text = text;
        this.errorMessage = errorMessage;
    }

    public static SpeechResult success(String text) {

        return new SpeechResult(
                true,
                text,
                null
        );
    }

    public static SpeechResult failure(
            String errorMessage
    ) {

        return new SpeechResult(
                false,
                null,
                errorMessage
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}