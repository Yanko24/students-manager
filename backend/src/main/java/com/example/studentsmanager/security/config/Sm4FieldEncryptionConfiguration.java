package com.example.studentsmanager.security.config;

import com.example.studentsmanager.security.Sm4FieldCipher;
import com.example.studentsmanager.security.Sm4FieldCipherProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Sm4FieldEncryptionConfiguration {
    @Bean
    public Sm4FieldCipher sm4FieldCipher(@Value("${security.sm4.key-base64:}") String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalStateException("SM4_KEY_BASE64 is required to encrypt user contact fields");
        }
        Sm4FieldCipher cipher = new Sm4FieldCipher(key.trim());
        Sm4FieldCipherProvider.configure(cipher);
        return cipher;
    }
}
