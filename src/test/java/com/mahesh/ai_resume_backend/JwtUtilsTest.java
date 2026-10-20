package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mahesh.ai_resume_backend.security.JwtUtils;
import com.mahesh.ai_resume_backend.security.UserPrincipal;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils("test_secret_key_that_is_at_least_32_characters_long_123456", 3600000L);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "testuser",
                "test@example.com",
                "hashedpassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtUtils.generateToken(principal);
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals("testuser", jwtUtils.getUsernameFromToken(token));
        assertEquals(1L, jwtUtils.getUserIdFromToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }
}
