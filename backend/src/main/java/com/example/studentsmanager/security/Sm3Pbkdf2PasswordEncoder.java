package com.example.studentsmanager.security;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Salted PBKDF2-HMAC-SM3 password encoder with BCrypt compatibility. */
public final class Sm3Pbkdf2PasswordEncoder implements PasswordEncoder {
    private static final String PREFIX = "{pbkdf2-sm3}";
    private static final String BOOTSTRAP_HASH = "{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA";
    private static final int ITERATIONS = 310_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        byte[] derived = derive(rawPassword, salt, ITERATIONS);
        return PREFIX + ITERATIONS + "$" + Base64.getUrlEncoder().withoutPadding().encodeToString(salt)
                + "$" + Base64.getUrlEncoder().withoutPadding().encodeToString(derived);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || rawPassword == null) {
            return false;
        }
        if (encodedPassword.startsWith(PREFIX)) {
            try {
                String[] parts = encodedPassword.substring(PREFIX.length()).split("\\$", -1);
                if (parts.length != 3) return false;
                int iterations = Integer.parseInt(parts[0]);
                if (iterations < 1 || iterations > 5_000_000) return false;
                byte[] salt = Base64.getUrlDecoder().decode(parts[1]);
                byte[] expected = Base64.getUrlDecoder().decode(parts[2]);
                if (salt.length < 16 || expected.length != KEY_BYTES) return false;
                return MessageDigest.isEqual(expected, derive(rawPassword, salt, iterations));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$")
                || encodedPassword.startsWith("$2y$")) {
            return bcrypt.matches(rawPassword, encodedPassword);
        }
        return false;
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return encodedPassword != null
                && (!encodedPassword.startsWith(PREFIX) || BOOTSTRAP_HASH.equals(encodedPassword));
    }

    private byte[] derive(CharSequence password, byte[] salt, int iterations) {
        char[] chars = password.toString().toCharArray();
        byte[] passwordBytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            passwordBytes[i * 2] = (byte) (chars[i] >>> 8);
            passwordBytes[i * 2 + 1] = (byte) chars[i];
        }
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SM3Digest());
        generator.init(passwordBytes, salt, iterations);
        byte[] derived = ((KeyParameter) generator.generateDerivedParameters(KEY_BYTES * 8)).getKey();
        java.util.Arrays.fill(chars, '\0');
        java.util.Arrays.fill(passwordBytes, (byte) 0);
        return derived;
    }
}
