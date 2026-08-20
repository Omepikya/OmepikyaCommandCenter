package com.omepikya.commandcenter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.omepikya.commandcenter.automation.ProactiveSettings;

public class ProactiveSettingsActivity
        extends AppCompatActivity {

    private ProactiveSettings settings;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setTitle(
                "Proactive Intelligence"
        );

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(
                            true
                    );
        }

        /*
         * Only the persistent settings object is used
         * here. No second operational ContextEngine
         * is created.
         */
        settings =
                new ProactiveSettings(
                        this
                );

        ScrollView scrollView =
                new ScrollView(
                        this
                );

        LinearLayout root =
                new LinearLayout(
                        this
                );

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(30)
        );

        TextView title =
                createText(
                        "Proactive Intelligence",
                        24,
                        true
                );

        root.addView(
                title
        );

        TextView description =
                createText(
                        "Control how Omepikya helps you "
                                + "without waiting for a command.",
                        15,
                        false
                );

        description.setPadding(
                0,
                dp(8),
                0,
                dp(20)
        );

        root.addView(
                description
        );

        addSwitch(
                root,
                "Proactive suggestions",
                "Allow Omepikya to suggest useful actions.",
                settings.isEnabled(),
                settings::setEnabled
        );

        addSwitch(
                root,
                "Behavior suggestions",
                "Use repeated command patterns to make suggestions.",
                settings.isBehaviorSuggestionsEnabled(),
                settings::setBehaviorSuggestionsEnabled
        );

        addSwitch(
                root,
                "Context suggestions",
                "Use recent commands and context for suggestions.",
                settings.isContextSuggestionsEnabled(),
                settings::setContextSuggestionsEnabled
        );

        addSwitch(
                root,
                "Automation suggestions",
                "Remind you about upcoming scheduled tasks.",
                settings.isAutomationSuggestionsEnabled(),
                settings::setAutomationSuggestionsEnabled
        );

        addSwitch(
                root,
                "Confirm sensitive actions",
                "Keep confirmation enabled before sensitive actions.",
                settings.shouldConfirmSensitiveActions(),
                settings::setConfirmSensitiveActions
        );

        TextView security =
                createText(
                        "Security: suggestions do not automatically "
                                + "authorize sensitive actions.",
                        14,
                        false
                );

        security.setPadding(
                0,
                dp(20),
                0,
                0
        );

        root.addView(
                security
        );

        scrollView.addView(
                root
        );

        setContentView(
                scrollView
        );
    }

    private void addSwitch(
            LinearLayout parent,
            String title,
            String description,
            boolean checked,
            OnChanged listener
    ) {

        LinearLayout row =
                new LinearLayout(
                        this
                );

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setPadding(
                0,
                dp(12),
                0,
                dp(12)
        );

        Switch toggle =
                new Switch(
                        this
                );

        toggle.setText(
                title
        );

        toggle.setTextSize(
                17
        );

        toggle.setChecked(
                checked
        );

        toggle.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView detail =
                createText(
                        description,
                        14,
                        false
                );

        detail.setPadding(
                0,
                dp(5),
                0,
                0
        );

        row.addView(
                toggle
        );

        row.addView(
                detail
        );

        toggle.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        listener.onChanged(
                                isChecked
                        )
        );

        parent.addView(
                row
        );
    }

    private TextView createText(
            String text,
            float size,
            boolean bold
    ) {

        TextView view =
                new TextView(
                        this
                );

        view.setText(
                text
        );

        view.setTextSize(
                size
        );

        if (bold) {

            view.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return view;
    }

    private int dp(
            float value
    ) {

        return (int)
                (
                        value
                                * getResources()
                                .getDisplayMetrics()
                                .density
                );
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    private interface OnChanged {

        void onChanged(
                boolean enabled
        );
    }
}