package com.example.studentsmanager.security.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

/** Identifies this backend process so tokens from an earlier process can be rejected. */
@Component
public class JwtSessionContext {
    public static final String CLAIM_NAME = "backendSession";

    private final String currentSessionId = UUID.randomUUID().toString();

    public String getCurrentSessionId() {
        return currentSessionId;
    }
}
