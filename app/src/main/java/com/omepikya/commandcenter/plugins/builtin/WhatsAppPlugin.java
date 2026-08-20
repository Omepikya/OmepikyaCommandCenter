package com.omepikya.commandcenter.plugins.builtin;

import android.content.Context;

import com.omepikya.commandcenter.contacts.ContactManager;
import com.omepikya.commandcenter.core.CommandContext;

import java.util.List;

/**
 * WhatsApp plugin with contact/number targeting.
 *
 * Examples:
 *
 * "WhatsApp John"
 * "open WhatsApp John"
 * "WhatsApp +919876543210"
 *
 * If no target is resolved, the generic WhatsApp URL is used.
 */
public final class WhatsAppPlugin
        extends UriPlugin {

    private final ContactManager contactManager;

    public WhatsAppPlugin(Context context) {

        super(context);

        contactManager =
                new ContactManager(context);
    }

    @Override
    public String getId() {
        return "whatsapp";
    }

    @Override
    public String getName() {
        return "WhatsApp";
    }

    @Override
    protected String[] keywords() {

        return new String[]{
                "whatsapp",
                "open whatsapp"
        };
    }

    @Override
    protected String uri() {

        return "https://wa.me/";
    }

    @Override
    protected String uriForCommand(
            CommandContext context
    ) {

        String query =
                extractWhatsAppTarget(
                        context
                );

        if (query.isEmpty()) {
            return uri();
        }

        String number =
                normalizeForWhatsApp(
                        query
                );

        if (number.isEmpty()) {

            number =
                    resolveContactNumber(
                            query
                    );
        }

        if (number == null
                || number.isEmpty()) {

            return uri();
        }

        return "https://wa.me/"
                + number;
    }

    private String extractWhatsAppTarget(
            CommandContext context
    ) {

        if (context == null
                || context.getRawCommand() == null) {

            return "";
        }

        String command =
                context.getRawCommand()
                        .trim();

        String lower =
                command.toLowerCase();

        String[] prefixes =
                new String[]{
                        "open whatsapp ",
                        "whatsapp ",
                        "message whatsapp ",
                        "message on whatsapp ",
                        "chat with "
                };

        for (String prefix : prefixes) {

            if (lower.startsWith(prefix)) {

                return command
                        .substring(
                                prefix.length()
                        )
                        .trim();
            }
        }

        return "";
    }

    private String resolveContactNumber(
            String query
    ) {

        try {

            List<ContactManager.ContactMatch>
                    matches =
                    contactManager.findContacts(
                            query
                    );

            if (matches == null
                    || matches.isEmpty()) {

                return null;
            }

            ContactManager.ContactMatch
                    match =
                    matches.get(0);

            if (match == null) {
                return null;
            }

            return normalizeForWhatsApp(
                    match.getPhoneNumber()
            );

        } catch (SecurityException e) {

            return null;

        } catch (Exception e) {

            return null;
        }
    }

    private String normalizeForWhatsApp(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String trimmed =
                value.trim();

        boolean hadPlus =
                trimmed.startsWith("+");

        String digits =
                trimmed.replaceAll(
                        "[^0-9]",
                        ""
                );

        if (digits.isEmpty()) {
            return "";
        }

        /*
         * Common Indian 10-digit number.
         */
        if (digits.length() == 10) {

            return "91" + digits;
        }

        /*
         * Indian number with leading zero.
         */
        if (digits.startsWith("0")
                && digits.length() == 11) {

            return "91"
                    + digits.substring(1);
        }

        /*
         * Already international.
         */
        if (hadPlus
                || digits.startsWith("91")) {

            return digits;
        }

        /*
         * Don't invent an international
         * country code.
         */
        return digits.length() >= 8
                && digits.length() <= 15
                ? digits
                : "";
    }
}