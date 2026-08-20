package com.omepikya.commandcenter.intelligence;

import java.util.Locale;

/**
 * Handles short conversational replies to pending
 * commands and follow-up questions.
 *
 * Designed to tolerate common Android speech-to-text
 * variations such as:
 *
 * "yes please"
 * "yes do it"
 * "yeah"
 * "yep"
 * "sure"
 * "go ahead"
 *
 * without accidentally treating a normal command as
 * a confirmation.
 */
public class FollowUpEngine {

    private static class EntityResolverOrdinalHelper {

        Integer ordinal(
                String text
        ) {

            switch (text) {

                case "1":
                case "one":
                case "first":
                    return 1;

                case "2":
                case "two":
                case "second":
                    return 2;

                case "3":
                case "three":
                case "third":
                    return 3;

                case "4":
                case "four":
                case "fourth":
                    return 4;

                case "5":
                case "five":
                case "fifth":
                    return 5;

                case "6":
                case "six":
                case "sixth":
                    return 6;

                case "7":
                case "seven":
                case "seventh":
                    return 7;

                case "8":
                case "eight":
                case "eighth":
                    return 8;

                case "9":
                case "nine":
                case "ninth":
                    return 9;

                case "10":
                case "ten":
                case "tenth":
                    return 10;

                default:
                    return null;
            }
        }
    }

    public enum Type {
        NONE,
        YES,
        NO,
        CANCEL,
        ORDINAL,
        REPEAT,
        CHANGE,
        UNKNOWN
    }

    public static class Reply {

        public final Type type;

        public final Integer ordinal;

        public Reply(
                Type type,
                Integer ordinal
        ) {

            this.type =
                    type;

            this.ordinal =
                    ordinal;
        }
    }

    public Reply classify(
            String text
    ) {

        if (text == null) {

            return new Reply(
                    Type.NONE,
                    null
            );
        }

        String value =
                normalize(
                        text
                );

        if (value.isEmpty()) {

            return new Reply(
                    Type.NONE,
                    null
            );
        }

        /*
         * ========================================================
         * YES
         * ========================================================
         */
        if (isYes(
                value
        )) {

            return new Reply(
                    Type.YES,
                    null
            );
        }

        /*
         * ========================================================
         * NO
         * ========================================================
         */
        if (isNo(
                value
        )) {

            return new Reply(
                    Type.NO,
                    null
            );
        }

        /*
         * ========================================================
         * CANCEL
         * ========================================================
         */
        if (isCancel(
                value
        )) {

            return new Reply(
                    Type.CANCEL,
                    null
            );
        }

        /*
         * ========================================================
         * REPEAT
         * ========================================================
         */
        if (isRepeat(
                value
        )) {

            return new Reply(
                    Type.REPEAT,
                    null
            );
        }

        /*
         * ========================================================
         * CHANGE
         * ========================================================
         */
        if (isChange(
                value
        )) {

            return new Reply(
                    Type.CHANGE,
                    null
            );
        }

        /*
         * ========================================================
         * ORDINAL
         * ========================================================
         */
        Integer ordinal =
                new EntityResolverOrdinalHelper()
                        .ordinal(
                                value
                        );

        if (ordinal != null) {

            return new Reply(
                    Type.ORDINAL,
                    ordinal
            );
        }

        return new Reply(
                Type.UNKNOWN,
                null
        );
    }

    private boolean isYes(
            String value
    ) {

        /*
         * Exact/common affirmative responses.
         */
        if (value.equals("yes")
                || value.equals("yeah")
                || value.equals("yep")
                || value.equals("yea")
                || value.equals("yup")
                || value.equals("yess")
                || value.equals("sure")
                || value.equals("okay")
                || value.equals("ok")
                || value.equals("confirm")
                || value.equals("confirmed")
                || value.equals("affirmative")
                || value.equals("go ahead")
                || value.equals("do it")
                || value.equals("please do")
                || value.equals("yes please")
                || value.equals("yeah please")
                || value.equals("yep please")
                || value.equals("yes do it")
                || value.equals("yes go ahead")
                || value.equals("yes confirm")
                || value.equals("that's right")
                || value.equals("that is right")
                || value.equals("correct")) {

            return true;
        }

        /*
         * Speech recognition sometimes produces punctuation
         * or a short phrase around the affirmative word.
         *
         * Only allow these patterns when the affirmative
         * word is the first token. This prevents arbitrary
         * commands containing "yes" from being classified
         * as confirmation.
         */
        return value.startsWith("yes ")
                || value.startsWith("yeah ")
                || value.startsWith("yep ")
                || value.startsWith("yea ")
                || value.startsWith("yup ")
                || value.startsWith("sure ")
                || value.startsWith("okay ")
                || value.startsWith("ok ");
    }

    private boolean isNo(
            String value
    ) {

        if (value.equals("no")
                || value.equals("nope")
                || value.equals("nah")
                || value.equals("no thanks")
                || value.equals("no thank you")
                || value.equals("don't")
                || value.equals("do not")
                || value.equals("not now")
                || value.equals("negative")) {

            return true;
        }

        return value.startsWith("no ")
                || value.startsWith("nope ");
                }

    private boolean isCancel(
            String value
    ) {

        return value.equals("cancel")
                || value.equals("stop")
                || value.equals("never mind")
                || value.equals("nevermind")
                || value.equals("forget it")
                || value.equals("forget that")
                || value.equals("cancel it");
    }

    private boolean isRepeat(
            String value
    ) {

        return value.equals("repeat")
                || value.equals("again")
                || value.equals("say that again")
                || value.equals("repeat that");
    }

    private boolean isChange(
            String value
    ) {

        return value.equals("change")
                || value.equals("change it")
                || value.equals("different one")
                || value.equals("something else")
                || value.equals("choose another");
    }

    private String normalize(
            String text
    ) {

        if (text == null) {
            return "";
        }

        String value =
                text.trim()
                        .toLowerCase(
                                Locale.US
                        )
                        .replaceAll(
                                "[.!?,;:]+",
                                " "
                        )
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .trim();

        return value;
    }
}