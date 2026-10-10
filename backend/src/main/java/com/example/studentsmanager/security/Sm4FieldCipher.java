package com.example.studentsmanager.security;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/** Authenticated SM4-GCM encryption for recoverable personal data fields. */
public final class Sm4FieldCipher {
    private static final String PREFIX = "SM4GCM:v1:";
    private static final int NONCE_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final byte[] key;

    public Sm4FieldCipher(String base64Key) {
        try {
            this.key = Base64.getDecoder().decode(base64Key);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("SM4_KEY_BASE64 must be valid Base64", e);
        }
        if (this.key.length != 16) {
            throw new IllegalArgumentException("SM4_KEY_BASE64 must decode to exactly 16 bytes");
        }
    }

    public String encrypt(String plaintext) {
        if (plaintext == null || isEncrypted(plaintext)) return plaintext;
        byte[] nonce = new byte[NONCE_BYTES];
        SECURE_RANDOM.nextBytes(nonce);
        byte[] input = plaintext.getBytes(StandardCharsets.UTF_8);
        try {
            GCMModeCipher cipher = GCMBlockCipher.newInstance(new SM4Engine());
            cipher.init(true, new AEADParameters(new KeyParameter(key), TAG_BITS, nonce));
            byte[] output = new byte[cipher.getOutputSize(input.length)];
            int length = cipher.processBytes(input, 0, input.length, output, 0);
            length += cipher.doFinal(output, length);
            byte[] payload = new byte[nonce.length + length];
            System.arraycopy(nonce, 0, payload, 0, nonce.length);
            System.arraycopy(output, 0, payload, nonce.length, length);
            Arrays.fill(input, (byte) 0);
            Arrays.fill(output, (byte) 0);
            return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
        } catch (InvalidCipherTextException e) {
            throw new IllegalStateException("SM4 encryption failed", e);
        }
    }

    public String decrypt(String value) {
        if (value == null || !isEncrypted(value)) return value;
        byte[] payload;
        try {
            payload = Base64.getUrlDecoder().decode(value.substring(PREFIX.length()));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid SM4 encrypted field", e);
        }
        if (payload.length < NONCE_BYTES + TAG_BITS / 8) {
            throw new IllegalStateException("Invalid SM4 encrypted field length");
        }
        byte[] nonce = Arrays.copyOfRange(payload, 0, NONCE_BYTES);
        byte[] input = Arrays.copyOfRange(payload, NONCE_BYTES, payload.length);
        try {
            GCMModeCipher cipher = GCMBlockCipher.newInstance(new SM4Engine());
            cipher.init(false, new AEADParameters(new KeyParameter(key), TAG_BITS, nonce));
            byte[] output = new byte[cipher.getOutputSize(input.length)];
            int length = cipher.processBytes(input, 0, input.length, output, 0);
            length += cipher.doFinal(output, length);
            String plaintext = new String(output, 0, length, StandardCharsets.UTF_8);
            Arrays.fill(output, (byte) 0);
            return plaintext;
        } catch (InvalidCipherTextException e) {
            throw new IllegalStateException("SM4 authentication failed; verify SM4_KEY_BASE64", e);
        }
    }

    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX);
    }
}
