package com.omepikya.commandcenter.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactManager {

    private final Context context;

    public ContactManager(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();
    }

    /**
     * Represents one contact/phone-number match.
     */
    public static class ContactMatch {

        private final String displayName;
        private final String phoneNumber;

        public ContactMatch(
                String displayName,
                String phoneNumber
        ) {

            this.displayName =
                    displayName;

            this.phoneNumber =
                    phoneNumber;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        @Override
        public String toString() {

            return displayName
                    + "\n"
                    + phoneNumber;
        }
    }

    /**
     * Finds contacts.
     *
     * Exact contact-name matches are preferred.
     *
     * Duplicate phone numbers are removed after
     * normalizing the number format.
     */
    public List<ContactMatch> findContacts(
            String contactName
    ) {

        List<ContactMatch> results =
                new ArrayList<>();

        if (contactName == null
                || contactName.trim().isEmpty()) {

            return results;
        }

        String name =
                contactName.trim();

        ContentResolver resolver =
                context.getContentResolver();

        /*
         * First search for an exact contact name.
         */
        queryContacts(
                resolver,
                name,
                true,
                results
        );

        /*
         * If exact matches were found,
         * don't mix them with partial matches.
         */
        if (!results.isEmpty()) {

            return removeDuplicates(
                    results
            );
        }

        /*
         * No exact match.
         *
         * Try partial matching.
         */
        queryContacts(
                resolver,
                name,
                false,
                results
        );

        return removeDuplicates(
                results
        );
    }

    private void queryContacts(
            ContentResolver resolver,
            String name,
            boolean exact,
            List<ContactMatch> results
    ) {

        Cursor cursor = null;

        try {

            String selection;

            String[] selectionArgs;

            if (exact) {

                selection =
                        ContactsContract
                                .CommonDataKinds
                                .Phone
                                .DISPLAY_NAME
                                + " = ? COLLATE NOCASE";

                selectionArgs =
                        new String[]{
                                name
                        };

            } else {

                selection =
                        ContactsContract
                                .CommonDataKinds
                                .Phone
                                .DISPLAY_NAME
                                + " LIKE ?";

                selectionArgs =
                        new String[]{
                                "%" + name + "%"
                        };
            }

            cursor =
                    resolver.query(

                            ContactsContract
                                    .CommonDataKinds
                                    .Phone
                                    .CONTENT_URI,

                            new String[]{
                                    ContactsContract
                                            .CommonDataKinds
                                            .Phone
                                            .DISPLAY_NAME,

                                    ContactsContract
                                            .CommonDataKinds
                                            .Phone
                                            .NUMBER
                            },

                            selection,

                            selectionArgs,

                            ContactsContract
                                    .CommonDataKinds
                                    .Phone
                                    .DISPLAY_NAME
                                    + " ASC"
                    );

            if (cursor == null) {
                return;
            }

            int nameIndex =
                    cursor.getColumnIndex(
                            ContactsContract
                                    .CommonDataKinds
                                    .Phone
                                    .DISPLAY_NAME
                    );

            int numberIndex =
                    cursor.getColumnIndex(
                            ContactsContract
                                    .CommonDataKinds
                                    .Phone
                                    .NUMBER
                    );

            if (nameIndex < 0
                    || numberIndex < 0) {

                return;
            }

            while (cursor.moveToNext()) {

                String displayName =
                        cursor.getString(
                                nameIndex
                        );

                String phoneNumber =
                        cursor.getString(
                                numberIndex
                        );

                if (displayName == null
                        || displayName.trim().isEmpty()) {

                    continue;
                }

                if (phoneNumber == null
                        || phoneNumber.trim().isEmpty()) {

                    continue;
                }

                results.add(
                        new ContactMatch(
                                displayName.trim(),
                                phoneNumber.trim()
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Removes duplicate contacts when the actual
     * phone number is the same but formatting differs.
     *
     * Examples treated as the same:
     *
     * 73180 94996
     * 7318094996
     *
     * +91 7318094996
     * +91-73180-94996
     */
    private List<ContactMatch> removeDuplicates(
            List<ContactMatch> contacts
    ) {

        List<ContactMatch> result =
                new ArrayList<>();

        Set<String> seenNumbers =
                new HashSet<>();

        for (ContactMatch contact : contacts) {

            String normalizedNumber =
                    normalizePhoneNumber(
                            contact.getPhoneNumber()
                    );

            /*
             * If normalization somehow produces
             * an empty value, fall back to the
             * original number.
             */
            if (normalizedNumber.isEmpty()) {

                normalizedNumber =
                        contact.getPhoneNumber()
                                .trim();
            }

            /*
             * The phone number is the important
             * identity here.
             *
             * Same number = same communication target.
             */
            if (seenNumbers.add(
                    normalizedNumber
            )) {

                result.add(
                        contact
                );
            }
        }

        return result;
    }

    /**
     * Normalizes a phone number for comparison.
     *
     * Removes:
     * - spaces
     * - hyphens
     * - brackets
     * - other formatting characters
     *
     * Keeps digits and an optional leading +.
     */
    private String normalizePhoneNumber(
            String phoneNumber
    ) {

        if (phoneNumber == null) {
            return "";
        }

        String value =
                phoneNumber.trim();

        boolean hasPlus =
                value.startsWith("+");

        String digits =
                value.replaceAll(
                        "[^0-9]",
                        ""
                );

        /*
         * Handle Indian numbers stored both as:
         *
         * 9876543210
         *
         * +919876543210
         *
         * 0919876543210
         *
         * without incorrectly treating unrelated
         * numbers as identical.
         */
        if (digits.startsWith("91")
                && digits.length() == 12) {

            return digits;
        }

        if (digits.length() == 10) {

            /*
             * Normalize Indian 10-digit numbers
             * to a common comparison form.
             */
            return "91" + digits;
        }

        if (digits.startsWith("0")
                && digits.length() == 11) {

            String withoutZero =
                    digits.substring(1);

            if (withoutZero.length() == 10) {

                return "91"
                        + withoutZero;
            }
        }

        /*
         * For international or unknown formats,
         * retain the digits.
         */
        if (hasPlus) {

            return "+"
                    + digits;
        }

        return digits;
    }

    /**
     * Returns a phone number only when exactly
     * one unique number matches.
     */
    public String findPhoneNumber(
            String contactName
    ) {

        List<ContactMatch> contacts =
                findContacts(
                        contactName
                );

        if (contacts.size() == 1) {

            return contacts
                    .get(0)
                    .getPhoneNumber();
        }

        return null;
    }

    /**
     * Returns the display name only when exactly
     * one unique number matches.
     */
    public String findDisplayName(
            String contactName
    ) {

        List<ContactMatch> contacts =
                findContacts(
                        contactName
                );

        if (contacts.size() == 1) {

            return contacts
                    .get(0)
                    .getDisplayName();
        }

        return null;
    }
}