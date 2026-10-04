package com.example.studentsmanager.security;

import com.example.studentsmanager.mapper.UserMapper;
import com.example.studentsmanager.model.dto.user.UserContactData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Sm4ContactDataMigration implements ApplicationRunner {
    private final UserMapper userMapper;
    private final Sm4FieldCipher cipher;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int migrated = 0;
        for (UserContactData contact : userMapper.selectAllContactData()) {
            boolean needsEncryption = (contact.getPhone() != null && !Sm4FieldCipher.isEncrypted(contact.getPhone()))
                    || (contact.getEmail() != null && !Sm4FieldCipher.isEncrypted(contact.getEmail()));
            if (needsEncryption) {
                contact.setPhone(cipher.encrypt(contact.getPhone()));
                contact.setEmail(cipher.encrypt(contact.getEmail()));
                migrated += userMapper.updateContactData(contact);
            }
        }
        if (migrated > 0) {
            log.info("Encrypted phone/email fields for {} user accounts with SM4-GCM", migrated);
        }
    }
}
