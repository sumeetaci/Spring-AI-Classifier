package com.restai.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
	// Generate a secure key: openssl rand -base64 32
    @Value("${application.security.jwt.secret-key}")
    private  String SECRET_KEY ; // = "your_very_secure_long_random_secret_key_here";

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        String tokenString=  Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey()) // Uses the decoded SecretKey object
                .compact();
        logger.info("Generated JWT token for user: " + username +" token: " + tokenString); 
        return tokenString;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
       /* return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload(); */
    	return Jwts.parserBuilder()           // Use parserBuilder() instead of parser()
    	        .setSigningKey(getSignInKey()) // Use setSigningKey() instead of verifyWith()
    	        .build()
    	        .parseClaimsJws(token)         // Use parseClaimsJws() instead of parseSignedClaims()
    	        .getBody();                    
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
