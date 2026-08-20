package com.omepikya.commandcenter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    private int dp(float value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * ---------------------------------------------------------
         * ACTION BAR
         * ---------------------------------------------------------
         */

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Omepikya Help");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*
         * ---------------------------------------------------------
         * COLORS
         * ---------------------------------------------------------
         */

        final int green =
                Color.rgb(0, 137, 123);

        final int darkText =
                Color.rgb(35, 40, 45);

        final int secondaryText =
                Color.rgb(90, 95, 100);

        final int white =
                Color.WHITE;

        final int lightGreen =
                Color.rgb(235, 248, 245);

        final int lightBlue =
                Color.rgb(238, 246, 255);

        final int lightYellow =
                Color.rgb(255, 249, 235);

        final int lightRed =
                Color.rgb(255, 241, 243);

        /*
         * ---------------------------------------------------------
         * ROOT SCROLL VIEW
         * ---------------------------------------------------------
         */

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);

        scrollView.setBackgroundColor(
                Color.rgb(250, 250, 250)
        );

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(28)
        );

        /*
         * ---------------------------------------------------------
         * HEADER
         * ---------------------------------------------------------
         */

        TextView title =
                textView(
                        "Omepikya Command Center",
                        28,
                        darkText,
                        Typeface.BOLD
                );

        title.setGravity(
                Gravity.CENTER
        );

        root.addView(
                title,
                matchWrap()
        );

        TextView subtitle =
                textView(
                        "Your smart assistant for Android",
                        16,
                        secondaryText,
                        Typeface.NORMAL
                );

        subtitle.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams subtitleParams =
                matchWrap();

        subtitleParams.topMargin =
                dp(5);

        root.addView(
                subtitle,
                subtitleParams
        );

        /*
         * Small green divider.
         */

        View divider =
                new View(this);

        GradientDrawable dividerBg =
                new GradientDrawable();

        dividerBg.setColor(
                green
        );

        dividerBg.setCornerRadius(
                dp(5)
        );

        divider.setBackground(
                dividerBg
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        dp(135),
                        dp(4)
                );

        dividerParams.gravity =
                Gravity.CENTER;

        dividerParams.topMargin =
                dp(12);

        dividerParams.bottomMargin =
                dp(18);

        root.addView(
                divider,
                dividerParams
        );

        /*
         * ---------------------------------------------------------
         * INTRO CARD
         * ---------------------------------------------------------
         */

        LinearLayout introCard =
                createCard(
                        lightGreen,
                        dp(16)
                );

        TextView introIcon =
                iconText(
                        "🤖",
                        34
                );

        introCard.addView(
                introIcon,
                new LinearLayout.LayoutParams(
                        dp(60),
                        dp(60)
                )
        );

        LinearLayout introText =
                new LinearLayout(this);

        introText.setOrientation(
                LinearLayout.VERTICAL
        );

        introText.setPadding(
                dp(14),
                0,
                0,
                0
        );

        TextView introTitle =
                textView(
                        "What is Omepikya?",
                        19,
                        darkText,
                        Typeface.BOLD
                );

        introText.addView(
                introTitle,
                matchWrap()
        );

        TextView introDescription =
                textView(
                        "Omepikya understands what you say or type "
                                + "and helps you perform tasks on your phone.",
                        15,
                        secondaryText,
                        Typeface.NORMAL
                );

        introDescription.setLineSpacing(
                0,
                1.15f
        );

        LinearLayout.LayoutParams introDescParams =
                matchWrap();

        introDescParams.topMargin =
                dp(5);

        introText.addView(
                introDescription,
                introDescParams
        );

        introCard.addView(
                introText,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        root.addView(
                introCard,
                cardParams(0, 18)
        );

        /*
         * ---------------------------------------------------------
         * 1. TEXT COMMANDS
         * ---------------------------------------------------------
         */

        LinearLayout textCard =
                createSectionCard(
                        lightBlue
                );

        addSectionHeader(
                textCard,
                "⌨️",
                "1. Text Commands",
                "Type what you want Omepikya to do."
        );

        TextView textInstruction =
                textView(
                        "Type your command in the command box "
                                + "and tap EXECUTE.",
                        15,
                        secondaryText,
                        Typeface.NORMAL
                );

        textInstruction.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );

        textCard.addView(
                textInstruction,
                matchWrap()
        );

        TextView examplesLabel =
                textView(
                        "Try these examples",
                        16,
                        green,
                        Typeface.BOLD
                );

        LinearLayout.LayoutParams examplesLabelParams =
                matchWrap();

        examplesLabelParams.topMargin =
                dp(10);

        examplesLabelParams.bottomMargin =
                dp(8);

        textCard.addView(
                examplesLabel,
                examplesLabelParams
        );

        addCommandRow(
                textCard,
                "▶️",
                "Open YouTube"
        );

        addCommandRow(
                textCard,
                "💬",
                "Open WhatsApp"
        );

        addCommandRow(
                textCard,
                "🔋",
                "Battery status"
        );

        addCommandRow(
                textCard,
                "📍",
                "Navigate to Patna"
        );

        addCommandRow(
                textCard,
                "🎵",
                "Play music"
        );

        addCommandRow(
                textCard,
                "🔎",
                "Search the web for photosynthesis"
        );

        root.addView(
                textCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 2. VOICE COMMANDS
         * ---------------------------------------------------------
         */

        LinearLayout voiceCard =
                createSectionCard(
                        lightRed
                );

        addSectionHeader(
                voiceCard,
                "🎤",
                "2. Voice Commands",
                "Talk to Omepikya instead of typing."
        );

        TextView voiceText =
                textView(
                        "Tap the 🎤 VOICE button and speak naturally.\n\n"
                                + "For example, you can say:\n"
                                + "\"Open YouTube\"\n"
                                + "\"Battery status\"\n"
                                + "\"Navigate to Patna\"",
                        15,
                        secondaryText,
                        Typeface.NORMAL
                );

        voiceText.setLineSpacing(
                0,
                1.15f
        );

        voiceCard.addView(
                voiceText,
                matchWrap()
        );

        root.addView(
                voiceCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 3. WHAT YOU CAN DO
         * ---------------------------------------------------------
         */

        LinearLayout capabilitiesCard =
                createSectionCard(
                        lightYellow
                );

        addSectionHeader(
                capabilitiesCard,
                "💡",
                "3. What You Can Do",
                "You can ask Omepikya for help with:"
        );

        addCapability(
                capabilitiesCard,
                "📱",
                "Apps & Settings"
        );

        addCapability(
                capabilitiesCard,
                "📞",
                "Calls & Messages"
        );

        addCapability(
                capabilitiesCard,
                "🧭",
                "Navigation"
        );

        addCapability(
                capabilitiesCard,
                "🔎",
                "Web Search"
        );

        addCapability(
                capabilitiesCard,
                "🎵",
                "Music & Media"
        );

        addCapability(
                capabilitiesCard,
                "🔋",
                "Device Information"
        );

        addCapability(
                capabilitiesCard,
                "⚙️",
                "Automation & More"
        );

        root.addView(
                capabilitiesCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 4. APPS & SETTINGS
         * ---------------------------------------------------------
         */

        LinearLayout appsCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                appsCard,
                "📱",
                "4. Apps & Settings",
                "Open apps or Android settings using simple commands."
        );

        addCommandRow(
                appsCard,
                "▶️",
                "Open YouTube"
        );

        addCommandRow(
                appsCard,
                "💬",
                "Open WhatsApp"
        );

        addCommandRow(
                appsCard,
                "🎵",
                "Open Spotify"
        );

        addCommandRow(
                appsCard,
                "📶",
                "Open Wi-Fi settings"
        );

        addCommandRow(
                appsCard,
                "🔵",
                "Open Bluetooth settings"
        );

        addCommandRow(
                appsCard,
                "🔊",
                "Open sound settings"
        );

        root.addView(
                appsCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 5. COMMUNICATION
         * ---------------------------------------------------------
         */

        LinearLayout communicationCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                communicationCard,
                "📞",
                "5. Calls & Messages",
                "Use Omepikya to start supported communication actions."
        );

        addCommandRow(
                communicationCard,
                "📞",
                "Call 9876543210"
        );

        addCommandRow(
                communicationCard,
                "💬",
                "Message 9876543210"
        );

        TextView communicationNote =
                textView(
                        "Important: calls and messages may require "
                                + "permission or confirmation.",
                        14,
                        secondaryText,
                        Typeface.ITALIC
                );

        communicationNote.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(5)
        );

        communicationCard.addView(
                communicationNote,
                matchWrap()
        );

        root.addView(
                communicationCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 6. NAVIGATION
         * ---------------------------------------------------------
         */

        LinearLayout navigationCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                navigationCard,
                "🧭",
                "6. Navigation",
                "Ask Omepikya to find a place or directions."
        );

        addCommandRow(
                navigationCard,
                "📍",
                "Navigate to Patna"
        );

        addCommandRow(
                navigationCard,
                "📍",
                "Directions to India Gate"
        );

        addCommandRow(
                navigationCard,
                "📍",
                "Take me to Nalanda"
        );

        root.addView(
                navigationCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 7. MEDIA
         * ---------------------------------------------------------
         */

        LinearLayout mediaCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                mediaCard,
                "🎵",
                "7. Music & Media",
                "Control supported media playback."
        );

        addCommandRow(
                mediaCard,
                "▶️",
                "Play music"
        );

        addCommandRow(
                mediaCard,
                "⏸️",
                "Pause music"
        );

        addCommandRow(
                mediaCard,
                "▶️",
                "Resume music"
        );

        addCommandRow(
                mediaCard,
                "⏭️",
                "Next track"
        );

        addCommandRow(
                mediaCard,
                "⏮️",
                "Previous track"
        );

        root.addView(
                mediaCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 8. WEB SEARCH
         * ---------------------------------------------------------
         */

        LinearLayout webCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                webCard,
                "🔎",
                "8. Web Search",
                "Ask Omepikya to search the web."
        );

        addCommandRow(
                webCard,
                "🔎",
                "Search the web for photosynthesis"
        );

        addCommandRow(
                webCard,
                "🌌",
                "Google space discoveries"
        );

        addCommandRow(
                webCard,
                "📚",
                "Search for BSc Botany notes"
        );

        root.addView(
                webCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 9. AUTOMATION
         * ---------------------------------------------------------
         */

        LinearLayout automationCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                automationCard,
                "⚙️",
                "9. Automation",
                "Omepikya can work with scheduled tasks and automation."
        );

        addCommandRow(
                automationCard,
                "⏰",
                "Remind me later"
        );

        addCommandRow(
                automationCard,
                "⏰",
                "Schedule a task"
        );

        TextView automationNote =
                textView(
                        "Automation features depend on the command "
                                + "and the Android permissions available.",
                        14,
                        secondaryText,
                        Typeface.ITALIC
                );

        automationNote.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(5)
        );

        automationCard.addView(
                automationNote,
                matchWrap()
        );

        root.addView(
                automationCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 10. EXECUTION & AUTONOMOUS CONTROL
         * ---------------------------------------------------------
         */

        LinearLayout executionCard =
                createSectionCard(
                        lightGreen
                );

        addSectionHeader(
                executionCard,
                "🧠",
                "10. Intelligent Execution",
                "Phase 9 adds deeper execution awareness and autonomous control."
        );

        addCapability(
                executionCard,
                "📋",
                "Execution history and persistent state"
        );

        addCapability(
                executionCard,
                "📊",
                "Live execution monitoring and progress"
        );

        addCapability(
                executionCard,
                "🔄",
                "Bounded failure recovery and retry"
        );

        addCapability(
                executionCard,
                "🧩",
                "Autonomous plan verification"
        );

        addCapability(
                executionCard,
                "🔁",
                "Replanning after recoverable failures"
        );

        addCapability(
                executionCard,
                "🛡️",
                "Safety-aware autonomous execution"
        );

        addCapability(
                executionCard,
                "💾",
                "Execution state recovery after interruption"
        );

        root.addView(
                executionCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 11. EXECUTION MONITOR
         * ---------------------------------------------------------
         */

        LinearLayout monitorCard =
                createSectionCard(
                        lightBlue
                );

        addSectionHeader(
                monitorCard,
                "📊",
                "11. Execution Monitor",
                "Omepikya can observe the current execution lifecycle."
        );

        addCommandRow(
                monitorCard,
                "▶️",
                "Current execution state"
        );

        addCommandRow(
                monitorCard,
                "📈",
                "Current step and progress"
        );

        addCommandRow(
                monitorCard,
                "🔄",
                "Retry and recovery status"
        );

        addCommandRow(
                monitorCard,
                "⏱️",
                "Execution duration"
        );

        addCommandRow(
                monitorCard,
                "⚠️",
                "Failure and safety state"
        );

        TextView monitorNote =
                textView(
                        "The monitor observes execution events. "
                                + "It does not bypass the normal command execution pipeline.",
                        14,
                        secondaryText,
                        Typeface.ITALIC
                );

        monitorNote.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(5)
        );

        monitorCard.addView(
                monitorNote,
                matchWrap()
        );

        root.addView(
                monitorCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 12. FAILURE RECOVERY
         * ---------------------------------------------------------
         */

        LinearLayout recoveryCard =
                createSectionCard(
                        lightYellow
                );

        addSectionHeader(
                recoveryCard,
                "🔄",
                "12. Failure Recovery",
                "Recoverable failures can be classified and handled safely."
        );

        addCapability(
                recoveryCard,
                "🔎",
                "Failure classification"
        );

        addCapability(
                recoveryCard,
                "🔁",
                "Bounded retry"
        );

        addCapability(
                recoveryCard,
                "🧩",
                "Replanning when a step cannot continue"
        );

        addCapability(
                recoveryCard,
                "🛑",
                "Stop instead of retrying unsafe operations"
        );

        addBullet(
                recoveryCard,
                "Safety, permission and confirmation failures are not blindly retried."
        );

        addBullet(
                recoveryCard,
                "Temporary, network and timeout failures may be eligible for bounded recovery."
        );

        root.addView(
                recoveryCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 13. AUTONOMOUS PLANNING
         * ---------------------------------------------------------
         */

        LinearLayout autonomousCard =
                createSectionCard(
                        white
                );

        addSectionHeader(
                autonomousCard,
                "🤖",
                "13. Autonomous Planning",
                "Complex goals can be broken into verified execution steps."
        );

        addCommandRow(
                autonomousCard,
                "🎯",
                "Give Omepikya a multi-step goal"
        );

        addCommandRow(
                autonomousCard,
                "🧩",
                "Break a goal into steps"
        );

        addCommandRow(
                autonomousCard,
                "✅",
                "Verify a plan before execution"
        );

        addCommandRow(
                autonomousCard,
                "🔁",
                "Replan after a recoverable failure"
        );

        addCapability(
                autonomousCard,
                "📝",
                "Goal decomposition"
        );

        addCapability(
                autonomousCard,
                "🧠",
                "Autonomous planning"
        );

        addCapability(
                autonomousCard,
                "✔️",
                "Step verification"
        );

        addCapability(
                autonomousCard,
                "🏁",
                "Final completion verification"
        );

        root.addView(
                autonomousCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 14. SAFETY & PERMISSIONS
         * ---------------------------------------------------------
         */

        LinearLayout safetyCard =
                createSectionCard(
                        lightRed
                );

        addSectionHeader(
                safetyCard,
                "🛡️",
                "14. Safety & Permissions",
                "Sensitive operations remain subject to safety and permission checks."
        );

        addCapability(
                safetyCard,
                "🔐",
                "Permission checks"
        );

        addCapability(
                safetyCard,
                "⚠️",
                "Risk-aware decisions"
        );

        addCapability(
                safetyCard,
                "✋",
                "Confirmation for sensitive operations"
        );

        addCapability(
                safetyCard,
                "🚫",
                "Safety holds for blocked operations"
        );

        addCapability(
                safetyCard,
                "👤",
                "User control over sensitive actions"
        );

        TextView safetyNote =
                textView(
                        "Omepikya should never bypass Android permissions "
                                + "or its safety layer just to complete a command.",
                        14,
                        secondaryText,
                        Typeface.ITALIC
                );

        safetyNote.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(5)
        );

        safetyCard.addView(
                safetyNote,
                matchWrap()
        );

        root.addView(
                safetyCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 15. EXECUTION EVENTS
         * ---------------------------------------------------------
         */

        LinearLayout eventsCard =
                createSectionCard(
                        lightGreen
                );

        addSectionHeader(
                eventsCard,
                "📡",
                "15. Execution Events",
                "The execution system uses a shared event stream for lifecycle monitoring."
        );

        addCapability(
                eventsCard,
                "📥",
                "Command received"
        );

        addCapability(
                eventsCard,
                "▶️",
                "Execution started"
        );

        addCapability(
                eventsCard,
                "🔄",
                "Recovery or replanning started"
        );

        addCapability(
                eventsCard,
                "⚠️",
                "Failure or timeout reported"
        );

        addCapability(
                eventsCard,
                "🛡️",
                "Safety hold reported"
        );

        addCapability(
                eventsCard,
                "🏁",
                "Execution completed"
        );

        TextView eventsNote =
                textView(
                        "Monitoring components observe the shared execution "
                                + "event bus instead of creating a separate execution pipeline.",
                        14,
                        secondaryText,
                        Typeface.ITALIC
                );

        eventsNote.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(5)
        );

        eventsCard.addView(
                eventsNote,
                matchWrap()
        );

        root.addView(
                eventsCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 16. RECOVERY AFTER INTERRUPTION
         * ---------------------------------------------------------
         */

        LinearLayout crashCard =
                createSectionCard(
                        lightBlue
                );

        addSectionHeader(
                crashCard,
                "💾",
                "16. Recovery After Interruption",
                "Persistent execution state helps the system understand interrupted work."
        );

        addBullet(
                crashCard,
                "Execution state can be persisted while work is in progress."
        );

        addBullet(
                crashCard,
                "A recoverable execution can be detected after an interruption."
        );

        addBullet(
                crashCard,
                "The system can inspect the saved state before deciding whether to resume, recover, replan or stop."
        );

        addBullet(
                crashCard,
                "Recovery does not automatically mean repeating a sensitive action."
        );

        root.addView(
                crashCard,
                cardParams(0, 14)
        );

        /*
         * ---------------------------------------------------------
         * 10. TIPS
         * ---------------------------------------------------------
         */

        LinearLayout tipsCard =
                createSectionCard(
                        lightBlue
                );

        addSectionHeader(
                tipsCard,
                "ℹ️",
                "Tips for Best Results",
                "A few simple things make commands work better."
        );

        addBullet(
                tipsCard,
                "Speak or type clearly."
        );

        addBullet(
                tipsCard,
                "Use normal, natural language."
        );

        addBullet(
                tipsCard,
                "You don't need to use special keywords."
        );

        addBullet(
                tipsCard,
                "If Omepikya doesn't understand, try saying "
                        + "the same thing in a different way."
        );

        addBullet(
                tipsCard,
                "For sensitive actions, follow the confirmation "
                        + "or permission prompts."
        );

        root.addView(
                tipsCard,
                cardParams(0, 18)
        );

        /*
         * ---------------------------------------------------------
         * FINAL MESSAGE
         * ---------------------------------------------------------
         */

        TextView footer =
                textView(
                        "♡  Made with Omepikya for smart assistance.",
                        14,
                        secondaryText,
                        Typeface.NORMAL
                );

        footer.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams footerParams =
                matchWrap();

        footerParams.topMargin =
                dp(4);

        footerParams.bottomMargin =
                dp(8);

        root.addView(
                footer,
                footerParams
        );

        scrollView.addView(
                root,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(
                scrollView
        );
    }

    /*
     * ============================================================
     * UI HELPERS
     * ============================================================
     */

    private TextView textView(
            String text,
            float size,
            int color,
            int style
    ) {

        TextView view =
                new TextView(this);

        view.setText(
                text
        );

        view.setTextSize(
                size
        );

        view.setTextColor(
                color
        );

        view.setTypeface(
                Typeface.DEFAULT,
                style
        );

        return view;
    }

    private TextView iconText(
            String icon,
            float size
    ) {

        TextView view =
                new TextView(this);

        view.setText(
                icon
        );

        view.setTextSize(
                size
        );

        view.setGravity(
                Gravity.CENTER
        );

        return view;
    }

    private LinearLayout createCard(
            int backgroundColor,
            int padding
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                backgroundColor
        );

        background.setCornerRadius(
                dp(18)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        225,
                        230,
                        230
                )
        );

        card.setBackground(
                background
        );

        return card;
    }

    private LinearLayout createSectionCard(
            int backgroundColor
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                backgroundColor
        );

        background.setCornerRadius(
                dp(18)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        228,
                        230,
                        232
                )
        );

        card.setBackground(
                background
        );

        return card;
    }

    private void addSectionHeader(
            LinearLayout parent,
            String icon,
            String title,
            String description
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView iconView =
                iconText(
                        icon,
                        28
                );

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                Color.WHITE
        );

        iconBackground.setCornerRadius(
                dp(14)
        );

        iconView.setBackground(
                iconBackground
        );

        row.addView(
                iconView,
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                )
        );

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        textContainer.setPadding(
                dp(13),
                0,
                0,
                0
        );

        TextView titleView =
                textView(
                        title,
                        20,
                        Color.rgb(
                                30,
                                35,
                                40
                        ),
                        Typeface.BOLD
                );

        textContainer.addView(
                titleView,
                matchWrap()
        );

        TextView descriptionView =
                textView(
                        description,
                        14,
                        Color.rgb(
                                90,
                                95,
                                100
                        ),
                        Typeface.NORMAL
                );

        descriptionView.setLineSpacing(
                0,
                1.1f
        );

        LinearLayout.LayoutParams descriptionParams =
                matchWrap();

        descriptionParams.topMargin =
                dp(3);

        textContainer.addView(
                descriptionView,
                descriptionParams
        );

        row.addView(
                textContainer,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        parent.addView(
                row,
                matchWrap()
        );
    }

    private void addCommandRow(
            LinearLayout parent,
            String icon,
            String command
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                dp(12),
                dp(9),
                dp(12),
                dp(9)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(12)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        232,
                        234,
                        236
                )
        );

        row.setBackground(
                background
        );

        TextView iconView =
                iconText(
                        icon,
                        21
                );

        row.addView(
                iconView,
                new LinearLayout.LayoutParams(
                        dp(36),
                        dp(36)
                )
        );

        TextView commandView =
                textView(
                        command,
                        15,
                        Color.rgb(
                                35,
                                40,
                                45
                        ),
                        Typeface.NORMAL
                );

        commandView.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams commandParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        commandParams.leftMargin =
                dp(8);

        row.addView(
                commandView,
                commandParams
        );

        LinearLayout.LayoutParams rowParams =
                matchWrap();

        rowParams.topMargin =
                dp(5);

        parent.addView(
                row,
                rowParams
        );
    }

    private void addCapability(
            LinearLayout parent,
            String icon,
            String label
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(12)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        238,
                        232,
                        218
                )
        );

        row.setBackground(
                background
        );

        TextView iconView =
                iconText(
                        icon,
                        20
                );

        row.addView(
                iconView,
                new LinearLayout.LayoutParams(
                        dp(38),
                        dp(38)
                )
        );

        TextView labelView =
                textView(
                        label,
                        15,
                        Color.rgb(
                                40,
                                43,
                                45
                        ),
                        Typeface.NORMAL
                );

        labelView.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        labelParams.leftMargin =
                dp(7);

        row.addView(
                labelView,
                labelParams
        );

        LinearLayout.LayoutParams rowParams =
                matchWrap();

        rowParams.topMargin =
                dp(6);

        parent.addView(
                row,
                rowParams
        );
    }

    private void addBullet(
            LinearLayout parent,
            String text
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.TOP
        );

        TextView bullet =
                textView(
                        "•",
                        18,
                        Color.rgb(
                                0,
                                137,
                                123
                        ),
                        Typeface.BOLD
                );

        bullet.setGravity(
                Gravity.CENTER
        );

        row.addView(
                bullet,
                new LinearLayout.LayoutParams(
                        dp(25),
                        dp(30)
                )
        );

        TextView content =
                textView(
                        text,
                        15,
                        Color.rgb(
                                70,
                                75,
                                80
                        ),
                        Typeface.NORMAL
                );

        content.setLineSpacing(
                0,
                1.15f
        );

        row.addView(
                content,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        LinearLayout.LayoutParams rowParams =
                matchWrap();

        rowParams.topMargin =
                dp(4);

        parent.addView(
                row,
                rowParams
        );
    }

    private LinearLayout.LayoutParams
    matchWrap() {

        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams
    cardParams(
            int top,
            int bottom
    ) {

        LinearLayout.LayoutParams params =
                matchWrap();

        params.topMargin =
                dp(top);

        params.bottomMargin =
                dp(bottom);

        return params;
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }
}