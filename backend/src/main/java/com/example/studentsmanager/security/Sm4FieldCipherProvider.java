package com.example.studentsmanager.security;

public final class Sm4FieldCipherProvider {
    private static volatile Sm4FieldCipher cipher;

    private Sm4FieldCipherProvider() {}

    public static void configure(Sm4FieldCipher configuredCipher) {
        cipher = configuredCipher;
    }

    public static Sm4FieldCipher get() {
        Sm4FieldCipher current = cipher;
        if (current == null) {
            throw new IllegalStateException("SM4 field cipher has not been configured");
        }
        return current;
    }
}
