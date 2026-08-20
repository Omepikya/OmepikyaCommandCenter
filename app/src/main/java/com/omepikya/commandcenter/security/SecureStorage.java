package com.omepikya.commandcenter.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

/**
 * Secure encrypted application storage.
 *
 * Values are encrypted using AES/GCM and an AES key stored inside
 * the Android Keystore.
 *
 * Legacy plaintext values are migrated to encrypted storage when
 * they are first read.
 */
public final class SecureStorage {

    private static final String PREF_NAME =
            "omepikya_secure_storage";

    private static final String KEY_ALIAS =
            "omepikya_secure_storage_key";

    private static final String VALUE_PREFIX =
            "v1:";

    private static final String KEYSTORE =
            "AndroidKeyStore";

    private static final String TRANSFORMATION =
            "AES/GCM/NoPadding";

    private static final int GCM_TAG_BITS =
            128;

    private final SharedPreferences preferences;

    public SecureStorage(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }

    public void putString(
            String key,
            String value
    ) {

        if (key == null
                || key.trim().isEmpty()) {
            return;
        }

        if (value == null) {
            remove(key);
            return;
        }

        String encrypted =
                encrypt(value);

        if (encrypted == null) {
            return;
        }

        preferences.edit()
                .putString(
                        key,
                        encrypted
                )
                .apply();
    }

    public String getString(
            String key
    ) {

        if (key == null
                || key.trim().isEmpty()) {
            return null;
        }

        String stored =
                preferences.getString(
                        key,
                        null
                );

        if (stored == null) {
            return null;
        }

        /*
         * Legacy plaintext migration.
         */
        if (!stored.startsWith(VALUE_PREFIX)) {

            String legacy =
                    stored;

            String encrypted =
                    encrypt(legacy);

            if (encrypted != null) {

                preferences.edit()
                        .putString(
                                key,
                                encrypted
                        )
                        .apply();
            }

            return legacy;
        }

        return decrypt(stored);
    }

    public void putBoolean(
            String key,
            boolean value
    ) {

        putString(
                key,
                Boolean.toString(value)
        );
    }

    public boolean getBoolean(
            String key,
            boolean defaultValue
    ) {

        String value =
                getString(key);

        if (value == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    public void remove(
            String key
    ) {

        if (key == null
                || key.trim().isEmpty()) {
            return;
        }

        preferences.edit()
                .remove(key)
                .apply();
    }

    public void clear() {

        preferences.edit()
                .clear()
                .apply();
    }

    public boolean contains(
            String key
    ) {

        if (key == null
                || key.trim().isEmpty()) {
            return false;
        }

        return preferences.contains(key);
    }

    private SecretKey getOrCreateKey()
            throws Exception {

        KeyStore keyStore =
                KeyStore.getInstance(
                        KEYSTORE
                );

        keyStore.load(null);

        if (keyStore.containsAlias(
                KEY_ALIAS
        )) {

            KeyStore.Entry entry =
                    keyStore.getEntry(
                            KEY_ALIAS,
                            null
                    );

            if (entry instanceof
                    KeyStore.SecretKeyEntry) {

                return ((KeyStore.SecretKeyEntry)
                        entry)
                        .getSecretKey();
            }
        }

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        KEYSTORE
                );

        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT
                                | KeyProperties.PURPOSE_DECRYPT
                )
                        .setBlockModes(
                                KeyProperties.BLOCK_MODE_GCM
                        )
                        .setEncryptionPaddings(
                                KeyProperties
                                        .ENCRYPTION_PADDING_NONE
                        )
                        .setKeySize(256)
                        .build()
        );

        return keyGenerator.generateKey();
    }

    private String encrypt(
            String value
    ) {

        try {

            Cipher cipher =
                    Cipher.getInstance(
                            TRANSFORMATION
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getOrCreateKey()
            );

            byte[] iv =
                    cipher.getIV();

            byte[] ciphertext =
                    cipher.doFinal(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            byte[] combined =
                    new byte[
                            iv.length
                                    + ciphertext.length
                    ];

            System.arraycopy(
                    iv,
                    0,
                    combined,
                    0,
                    iv.length
            );

            System.arraycopy(
                    ciphertext,
                    0,
                    combined,
                    iv.length,
                    ciphertext.length
            );

            return VALUE_PREFIX
                    + Base64.encodeToString(
                            combined,
                            Base64.NO_WRAP
                    );

        } catch (Exception e) {

            return null;
        }
    }

    private String decrypt(
            String value
    ) {

        try {

            String encoded =
                    value.substring(
                            VALUE_PREFIX.length()
                    );

            byte[] combined =
                    Base64.decode(
                            encoded,
                            Base64.NO_WRAP
                    );

            if (combined.length < 13) {
                return null;
            }

            byte[] iv =
                    new byte[12];

            byte[] ciphertext =
                    new byte[
                            combined.length
                                    - iv.length
                    ];

            System.arraycopy(
                    combined,
                    0,
                    iv,
                    0,
                    iv.length
            );

            System.arraycopy(
                    combined,
                    iv.length,
                    ciphertext,
                    0,
                    ciphertext.length
            );

            Cipher cipher =
                    Cipher.getInstance(
                            TRANSFORMATION
                    );

            javax.crypto.spec.GCMParameterSpec
                    gcmSpec =
                    new javax.crypto.spec.GCMParameterSpec(
                            GCM_TAG_BITS,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getOrCreateKey(),
                    gcmSpec
            );

            byte[] plaintext =
                    cipher.doFinal(
                            ciphertext
                    );

            return new String(
                    plaintext,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            return null;
        }
    }
}