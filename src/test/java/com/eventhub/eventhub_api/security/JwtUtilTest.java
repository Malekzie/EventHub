package com.eventhub.eventhub_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // 41 characters — satisfies JJWT's 256-bit minimum for HMAC-SHA256
        jwtUtil = new JwtUtil("test-secret-key-for-testing-only-32chars!", 86400000L);
    }

    @Test
    void generateToken_producesValidToken() {
        String token = jwtUtil.generateToken("alice@example.com");

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    void extractEmail_returnsSubject() {
        String token = jwtUtil.generateToken("alice@example.com");

        assertThat(jwtUtil.extractEmail(token)).isEqualTo("alice@example.com");
    }

    @Test
    void isTokenValid_returnsFalseForGarbage() {
        assertThat(jwtUtil.isTokenValid("not.a.valid.token")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        // Expiry of -1 ms means the token is already expired when generated
        JwtUtil shortLived = new JwtUtil("test-secret-key-for-testing-only-32chars!", -1L);
        String token = shortLived.generateToken("alice@example.com");

        assertThat(jwtUtil.isTokenValid(token)).isFalse();
    }
}
