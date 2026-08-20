package com.omepikya.commandcenter.router;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.omepikya.commandcenter.contacts.ContactManager;
import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

import java.net.URLEncoder;
import java.util.List;

/**
 * Handles calls, SMS drafts and WhatsApp intents.
 *
 * This implementation intentionally uses ACTION_DIAL/ACTION_SENDTO instead
 * of directly placing calls or sending SMS. That avoids dangerous runtime
 * permissions and gives the user the final send/call control.
 */
public class CommunicationAction implements Action {

    private final Context context;
    private final ContactManager contactManager;

    public CommunicationAction(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        this.context = context.getApplicationContext();
        this.contactManager = new ContactManager(context);
    }

    @Override
    public String getName() {
        return "Communication";
    }

    @Override
    public boolean canHandle(CommandContext commandContext) {
        return commandContext != null
                && commandContext.getCommandType() == CommandType.COMMUNICATION;
    }

    @Override
    public CommandResult execute(CommandContext commandContext) {
        if (commandContext == null) {
            return CommandResult.failure("Communication command is unavailable.");
        }

        String command = commandContext.getRawCommand();
        if (command == null || command.trim().isEmpty()) {
            return CommandResult.failure("No communication command was specified.");
        }

        CommunicationRequest request = parseCommand(command);

        if (request.target.isEmpty()) {
            return CommandResult.failure(
                    "Please specify a contact name or phone number."
            );
        }

        if (looksLikePhoneNumber(request.target)) {
            return openCommunication(
                    request.type,
                    request.target,
                    request.target,
                    request.message
            );
        }

        List<ContactManager.ContactMatch> contacts =
                contactManager.findContacts(request.target);

        if (contacts.isEmpty()) {
            return CommandResult.failure(
                    "I couldn't find " + request.target + " in your contacts."
            );
        }

        if (contacts.size() == 1) {
            ContactManager.ContactMatch contact = contacts.get(0);
            return openCommunication(
                    request.type,
                    contact.getPhoneNumber(),
                    contact.getDisplayName(),
                    request.message
            );
        }

        showContactChooser(request, contacts);
        return CommandResult.success(
                "Multiple contacts found. Please choose one."
        );
    }

    private void showContactChooser(
            final CommunicationRequest request,
            final List<ContactManager.ContactMatch> contacts
    ) {
        if (!(context instanceof Activity)) {
            return;
        }

        final Activity activity = (Activity) context;
        if (activity.isFinishing()
                || (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed())) {
            return;
        }

        final Runnable showDialog = () -> {
            if (activity.isFinishing()
                    || (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
                    && activity.isDestroyed())) {
                return;
            }

            String[] choices = new String[contacts.size()];
            for (int i = 0; i < contacts.size(); i++) {
                ContactManager.ContactMatch contact = contacts.get(i);
                choices[i] = contact.getDisplayName()
                        + "\n"
                        + contact.getPhoneNumber();
            }

            new AlertDialog.Builder(activity)
                    .setTitle("Choose " + request.target)
                    .setItems(choices, (dialogInterface, which) -> {
                        if (which < 0 || which >= contacts.size()) {
                            return;
                        }

                        ContactManager.ContactMatch selected = contacts.get(which);
                        openCommunication(
                                request.type,
                                selected.getPhoneNumber(),
                                selected.getDisplayName(),
                                request.message
                        );
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
        };

        if (activity.isFinishing()) {
            return;
        }

        activity.runOnUiThread(showDialog);
    }

    private CommandResult openCommunication(
            String type,
            String phoneNumber,
            String displayName,
            String message
    ) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return CommandResult.failure("The phone number is invalid.");
        }

        try {
            if ("call".equals(type)) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel", phoneNumber.trim(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return CommandResult.success(
                        "Opening the phone dialer for " + displayName
                );
            }

            if ("whatsapp".equals(type)) {
                String whatsappNumber = normalizeWhatsAppNumber(phoneNumber);
                if (whatsappNumber.isEmpty()) {
                    return CommandResult.failure("The WhatsApp number is invalid.");
                }

                String encodedMessage = message == null ? "" : message.trim();
                String url = "https://wa.me/" + whatsappNumber;

                if (!encodedMessage.isEmpty()) {
                    url += "?text=" + URLEncoder.encode(encodedMessage, "UTF-8");
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage("com.whatsapp");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return CommandResult.success(
                        "Opening WhatsApp for " + displayName
                );
            }

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.fromParts("smsto", phoneNumber.trim(), null));

            if (message != null && !message.trim().isEmpty()) {
                intent.putExtra("sms_body", message.trim());
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return CommandResult.success(
                    "Opening messages for " + displayName
            );

        } catch (Exception e) {
            return CommandResult.failure(
                    "Android could not open the requested communication app."
            );
        }
    }

    private CommunicationRequest parseCommand(String command) {
        String text = command.trim();
        String lower = text.toLowerCase();

        String type = "sms";
        String remainder = text;

        if (lower.startsWith("call ")
                || lower.startsWith("phone ")
                || lower.startsWith("dial ")
                || lower.startsWith("ring ")) {

            type = "call";
            remainder = text.substring(text.indexOf(' ') + 1).trim();

        } else if (lower.startsWith("give ") && lower.contains(" a call")) {

            type = "call";
            remainder = text.substring(5).trim();
            int callIndex = remainder.toLowerCase().indexOf(" a call");
            if (callIndex >= 0) {
                remainder = remainder.substring(0, callIndex).trim();
            }

        } else if (lower.contains("whatsapp")) {

            type = "whatsapp";
            remainder = removeMessagePrefix(text);

        } else {

            remainder = removeMessagePrefix(text);
        }

        if ("call".equals(type)) {
            return new CommunicationRequest(type, remainder, "");
        }

        return splitTargetAndMessage(type, remainder);
    }

    private String removeMessagePrefix(String text) {
        String lower = text.toLowerCase();
        String[] prefixes = {
                "send a whatsapp message to ",
                "send whatsapp message to ",
                "send an sms to ",
                "send sms to ",
                "send a message to ",
                "send message to ",
                "whatsapp ",
                "message ",
                "text ",
                "sms "
        };

        for (String prefix : prefixes) {
            if (lower.startsWith(prefix)) {
                return text.substring(prefix.length()).trim();
            }
        }

        return text;
    }

    private CommunicationRequest splitTargetAndMessage(
            String type,
            String remainder
    ) {
        List<ContactManager.ContactMatch> exact =
                contactManager.findContacts(remainder);

        if (!exact.isEmpty()) {
            return new CommunicationRequest(type, remainder, "");
        }

        String[] words = remainder.split("\\s+");

        for (int end = words.length - 1; end >= 1; end--) {
            StringBuilder candidate = new StringBuilder();

            for (int i = 0; i < end; i++) {
                if (i > 0) {
                    candidate.append(' ');
                }
                candidate.append(words[i]);
            }

            String target = candidate.toString().trim();
            List<ContactManager.ContactMatch> matches =
                    contactManager.findContacts(target);

            if (!matches.isEmpty()) {
                StringBuilder body = new StringBuilder();
                for (int i = end; i < words.length; i++) {
                    if (body.length() > 0) {
                        body.append(' ');
                    }
                    body.append(words[i]);
                }

                return new CommunicationRequest(
                        type,
                        target,
                        body.toString().trim()
                );
            }
        }

        return new CommunicationRequest(type, remainder, "");
    }

    private boolean looksLikePhoneNumber(String value) {
        if (value == null) {
            return false;
        }

        String cleaned = value.replaceAll("[^0-9]", "");
        return cleaned.matches("\\d{7,15}");
    }

    private String normalizeWhatsAppNumber(String phoneNumber) {
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        if (digits.length() == 10) {
            return "91" + digits;
        }

        if (digits.startsWith("0") && digits.length() == 11) {
            return "91" + digits.substring(1);
        }

        return digits;
    }

    private static class CommunicationRequest {
        final String type;
        final String target;
        final String message;

        CommunicationRequest(String type, String target, String message) {
            this.type = type;
            this.target = target == null ? "" : target.trim();
            this.message = message == null ? "" : message.trim();
        }
    }
}