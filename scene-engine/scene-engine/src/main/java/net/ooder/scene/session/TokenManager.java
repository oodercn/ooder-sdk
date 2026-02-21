package net.ooder.scene.session;

import java.util.Map;

public interface TokenManager {

    TokenInfo generateToken(String subject);

    TokenInfo generateToken(String subject, Map<String, Object> claims);

    TokenInfo validateToken(String token);

    TokenInfo refreshToken(String refreshToken);

    void revokeToken(String token);

    boolean isTokenRevoked(String token);

    TokenInfo parseToken(String token);
}
