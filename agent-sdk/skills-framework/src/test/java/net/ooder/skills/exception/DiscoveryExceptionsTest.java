package net.ooder.skills.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscoveryExceptionTest {

    @Test
    @DisplayName("测试 DiscoveryException - 消息构造")
    void testDiscoveryExceptionWithMessage() {
        DiscoveryException ex = new DiscoveryException("Test error");
        assertEquals("DISCOVERY_ERROR", ex.getErrorCode());
        assertEquals("Test error", ex.getMessage());
    }

    @Test
    @DisplayName("测试 DiscoveryException - 错误码和消息")
    void testDiscoveryExceptionWithCodeAndMessage() {
        DiscoveryException ex = new DiscoveryException("CUSTOM_CODE", "Custom error");
        assertEquals("CUSTOM_CODE", ex.getErrorCode());
        assertEquals("Custom error", ex.getMessage());
    }

    @Test
    @DisplayName("测试 DiscoveryException - 消息和原因")
    void testDiscoveryExceptionWithMessageAndCause() {
        Exception cause = new RuntimeException("Root cause");
        DiscoveryException ex = new DiscoveryException("Test error", cause);
        assertEquals("DISCOVERY_ERROR", ex.getErrorCode());
        assertEquals("Test error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}

class AuthenticationExceptionTest {

    @Test
    @DisplayName("测试 AuthenticationException - 消息")
    void testAuthenticationExceptionWithMessage() {
        AuthenticationException ex = new AuthenticationException("Auth failed");
        assertEquals("AUTHENTICATION_ERROR", ex.getErrorCode());
        assertEquals("Auth failed", ex.getMessage());
    }

    @Test
    @DisplayName("测试 AuthenticationException - 消息和原因")
    void testAuthenticationExceptionWithMessageAndCause() {
        Exception cause = new RuntimeException("Invalid token");
        AuthenticationException ex = new AuthenticationException("Auth failed", cause);
        assertEquals("AUTHENTICATION_ERROR", ex.getErrorCode());
        assertEquals("Auth failed", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}

class RepositoryNotFoundExceptionTest {

    @Test
    @DisplayName("测试 RepositoryNotFoundException")
    void testRepositoryNotFoundException() {
        RepositoryNotFoundException ex = new RepositoryNotFoundException("ooderCN", "skills");
        assertEquals("REPOSITORY_NOT_FOUND", ex.getErrorCode());
        assertEquals("ooderCN", ex.getOwner());
        assertEquals("skills", ex.getRepo());
        assertTrue(ex.getMessage().contains("ooderCN"));
        assertTrue(ex.getMessage().contains("skills"));
    }
}

class ApiRateLimitExceptionTest {

    @Test
    @DisplayName("测试 ApiRateLimitException")
    void testApiRateLimitException() {
        ApiRateLimitException ex = new ApiRateLimitException("gitee", 60);
        assertEquals("API_RATE_LIMIT", ex.getErrorCode());
        assertEquals(60, ex.getRetryAfterSeconds());
        assertTrue(ex.getMessage().contains("gitee"));
        assertTrue(ex.getMessage().contains("60"));
    }
}
