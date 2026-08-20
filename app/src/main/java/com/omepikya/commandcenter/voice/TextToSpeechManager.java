package com.omepikya.commandcenter.voice;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

/**
 * Lifecycle-safe TextToSpeech manager.
 *
 * BUG-05:
 * - Uses Application Context instead of Activity Context.
 * - Prevents duplicate initialization.
 * - Prevents use after shutdown.
 * - Makes shutdown idempotent.
 * - Cleans up the native TTS engine.
 * - Protects callbacks after shutdown.
 *
 * Listener API intentionally remains compatible with VoiceEngine.
 */
public class TextToSpeechManager
        implements TextToSpeech.OnInitListener {

    private static final String DEFAULT_UTTERANCE_ID =
            "OMEPIKYA_RESPONSE";

    private final Context applicationContext;

    private TextToSpeech textToSpeech;

    private boolean initialized = false;

    private boolean initializing = false;

    private boolean shuttingDown = false;

    private boolean shutdownComplete = false;

    private Listener listener;

    public interface Listener {

        void onReady();

        void onError(String error);

        void onStart();

        void onComplete();
    }

    public TextToSpeechManager(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        /*
         * Critical BUG-05 fix:
         *
         * Never retain the Activity context.
         */
        applicationContext =
                context.getApplicationContext();

        initialize();
    }

    private synchronized void initialize() {

        if (shuttingDown
                || shutdownComplete) {
            return;
        }

        /*
         * Prevent double initialization.
         */
        if (textToSpeech != null
                || initializing) {
            return;
        }

        initializing = true;

        try {

            textToSpeech =
                    new TextToSpeech(
                            applicationContext,
                            this
                    );

        } catch (Exception e) {

            initializing = false;
            initialized = false;
            textToSpeech = null;

            notifyError(
                    "Text-to-speech initialization failed."
            );
        }
    }

    @Override
    public synchronized void onInit(
            int status
    ) {

        initializing = false;

        /*
         * The Activity may have been destroyed while
         * Android was initializing the TTS engine.
         */
        if (shuttingDown
                || shutdownComplete) {

            return;
        }

        if (status
                != TextToSpeech.SUCCESS) {

            initialized = false;

            notifyError(
                    "Text-to-speech initialization failed."
            );

            return;
        }

        if (textToSpeech == null) {

            initialized = false;

            return;
        }

        try {

            int result =
                    textToSpeech.setLanguage(
                            Locale.getDefault()
                    );

            /*
             * Fall back to English if the device's
             * default locale is unavailable.
             */
            if (result
                    == TextToSpeech.LANG_MISSING_DATA
                    || result
                    == TextToSpeech.LANG_NOT_SUPPORTED) {

                result =
                        textToSpeech.setLanguage(
                                Locale.US
                        );
            }

            if (result
                    == TextToSpeech.LANG_MISSING_DATA
                    || result
                    == TextToSpeech.LANG_NOT_SUPPORTED) {

                initialized = false;

                notifyError(
                        "No supported text-to-speech language is available."
                );

                return;
            }

            setupProgressListener();

            initialized = true;

            notifyReady();

        } catch (Exception e) {

            initialized = false;

            notifyError(
                    "Text-to-speech setup failed."
            );
        }
    }

    private synchronized void setupProgressListener() {

        if (textToSpeech == null
                || shuttingDown
                || shutdownComplete) {

            return;
        }

        textToSpeech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {

                    @Override
                    public void onStart(
                            String utteranceId
                    ) {

                        synchronized (
                                TextToSpeechManager.this
                        ) {

                            if (shuttingDown
                                    || shutdownComplete
                                    || !initialized) {

                                return;
                            }
                        }

                        notifyStart();
                    }

                    @Override
                    public void onDone(
                            String utteranceId
                    ) {

                        synchronized (
                                TextToSpeechManager.this
                        ) {

                            if (shuttingDown
                                    || shutdownComplete) {

                                return;
                            }
                        }

                        notifyComplete();
                    }

                    @Override
                    public void onError(
                            String utteranceId
                    ) {

                        synchronized (
                                TextToSpeechManager.this
                        ) {

                            if (shuttingDown
                                    || shutdownComplete) {

                                return;
                            }
                        }

                        notifyError(
                                "Text-to-speech playback failed."
                        );
                    }
                }
        );
    }

    public synchronized void setListener(
            Listener listener
    ) {

        if (shuttingDown
                || shutdownComplete) {

            return;
        }

        this.listener = listener;
    }

    public synchronized boolean isInitialized() {

        return initialized
                && textToSpeech != null
                && !shuttingDown
                && !shutdownComplete;
    }

    public synchronized boolean isReady() {

        return isInitialized();
    }

    public synchronized void speak(
            String text,
            String utteranceId
    ) {

        if (text == null
                || text.trim().isEmpty()) {

            return;
        }

        if (!isInitialized()) {

            return;
        }

        if (utteranceId == null
                || utteranceId.trim().isEmpty()) {

            utteranceId =
                    DEFAULT_UTTERANCE_ID;
        }

        try {

            if (Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.LOLLIPOP) {

                textToSpeech.speak(
                        text.trim(),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        utteranceId
                );

            } else {

                HashMap<String, String> params =
                        new HashMap<>();

                params.put(
                        TextToSpeech.Engine
                                .KEY_PARAM_UTTERANCE_ID,
                        utteranceId
                );

                textToSpeech.speak(
                        text.trim(),
                        TextToSpeech.QUEUE_FLUSH,
                        params
                );
            }

        } catch (IllegalStateException e) {

            /*
             * TTS can become invalid asynchronously.
             * Never allow that to crash the app.
             */
            initialized = false;

            notifyError(
                    "Text-to-speech is no longer available."
            );

        } catch (Exception e) {

            notifyError(
                    "Unable to start text-to-speech."
            );
        }
    }

    public synchronized void speak(
            String text
    ) {

        speak(
                text,
                DEFAULT_UTTERANCE_ID
        );
    }

    public synchronized void stop() {

        if (textToSpeech == null
                || !initialized
                || shuttingDown
                || shutdownComplete) {

            return;
        }

        try {

            textToSpeech.stop();

        } catch (Exception ignored) {
        }
    }

    /**
     * Idempotent cleanup.
     *
     * Safe to call from Activity.onDestroy() multiple times.
     */
    public synchronized void shutdown() {

        if (shutdownComplete) {
            return;
        }

        shuttingDown = true;

        initialized = false;

        initializing = false;

        TextToSpeech engine =
                textToSpeech;

        textToSpeech = null;

        /*
         * Remove listener reference so the Activity is not
         * retained by this manager after destruction.
         */
        listener = null;

        if (engine != null) {

            try {
                engine.stop();
            } catch (Exception ignored) {
            }

            try {

                if (Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.LOLLIPOP) {

                    engine.setOnUtteranceProgressListener(
                            null
                    );
                }

            } catch (Exception ignored) {
            }

            try {
                engine.shutdown();
            } catch (Exception ignored) {
            }
        }

        shutdownComplete = true;
    }

    public synchronized void clearListener() {

        listener = null;
    }

    private synchronized void notifyReady() {

        if (shuttingDown
                || shutdownComplete) {
            return;
        }

        Listener current =
                listener;

        if (current == null) {
            return;
        }

        try {
            current.onReady();
        } catch (Exception ignored) {
        }
    }

    private synchronized void notifyStart() {

        if (shuttingDown
                || shutdownComplete) {
            return;
        }

        Listener current =
                listener;

        if (current == null) {
            return;
        }

        try {
            current.onStart();
        } catch (Exception ignored) {
        }
    }

    private synchronized void notifyComplete() {

        if (shuttingDown
                || shutdownComplete) {
            return;
        }

        Listener current =
                listener;

        if (current == null) {
            return;
        }

        try {
            current.onComplete();
        } catch (Exception ignored) {
        }
    }

    private synchronized void notifyError(
            String error
    ) {

        if (shuttingDown
                || shutdownComplete) {
            return;
        }

        Listener current =
                listener;

        if (current == null) {
            return;
        }

        try {
            current.onError(
                    error == null
                            ? "Text-to-speech error."
                            : error
            );
        } catch (Exception ignored) {
        }
    }
}