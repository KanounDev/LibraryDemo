package com.example.library.security;

import com.example.library.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// This service handles JWT creation and validation.
@Service
public class JwtService {

    // Secret key from application.properties (keep it secure!).
    @Value("${jwt.secret}")
    private String secretKey;

    // Token expiration time in milliseconds (24 hours).
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // secretKey is your base64 string from properties
        return Keys.hmacShaKeyFor(keyBytes); // this returns SecretKey (HS256/384/512 compatible)
    }

    // Generates a JWT token for the user.
    // Includes user details like ID, role, branch in the payload.
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.id); // Add user ID.
        claims.put("role", user.role); // Add role.
        claims.put("branch", user.branch); // Add branch if applicable.

        // Build the JWT with subject (username), claims, issued time, expiration (e.g.,
        // 24 hours), and sign it.
        return Jwts.builder()
                .claims(claims) // ← claims() instead of setClaims()
                .subject(user.username) // ← subject() instead of setSubject()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey()) // ← no algorithm needed (infers HS256 from key type)
                .compact();
    }

    // Extracts username from token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generic method to extract any claim from token.

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parses the token and gets all claims.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // now matches: SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Gets the signing key from the secret.

}