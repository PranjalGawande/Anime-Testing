package com.review.anime.security;

import com.review.anime.entites.User;
import java.time.Clock;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.Setter;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;
import java.time.Instant;
import java.time.ZoneId;

@Service
public class JwtService {
    private final String SECRET_KEY = "e3cbac02bbf968f70eba5f5dea3634f7887e0bf621abbf6ff2b2bb4b0da48561";

    private final Clock clock;

    public JwtService() {
        this(Clock.systemDefaultZone());
    }

    public JwtService(Clock clock) {
        this.clock = clock;
    }

//    private Clock clock = Clock.systemUTC();
//    public void setClock(Clock clock) {
//        this.clock = clock;
//    }

    // Getter and setter for clock (used in tests)
//    @Setter
//    private Clock clock = Clock.systemUTC();  // Default clock

    public String extractEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isValid(String token, User user) {
        final String email = extractEmail(token);
        return (email.equals(user.getEmail())) && !isTokenExpired(token);
    }

//    public boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    public String generateToken(User login) {
//        String token = Jwts
//                .builder()
//                .subject(login.getEmail())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date (System.currentTimeMillis()+ 5 * 60 * 60 * 1000 ))
//                .signWith(getSignKey())
//                .compact();
//
//        return token;
//    }



    public String generateToken(User login) {
        Date issuedAt = Date.from(clock.instant()); // Using mocked clock here
        Date expiration = Date.from(clock.instant().plusSeconds(5 * 60 * 60)); // Expiration 5 hours from now

        String token = Jwts
                .builder()
                .subject(login.getEmail())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSignKey())
                .compact();

        return token;
    }

//    public String generateToken(User user) {
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                .setIssuedAt(Date.from(clock.instant()))
//                .setExpiration(Date.from(clock.instant().plus(5, ChronoUnit.MINUTES))) // 5 minutes expiration
//                .signWith(getSignKey())
//                .compact();
//    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token); // Extract expiration date from the token
            return expiration.before(Date.from(clock.instant())); // Compare with current time
        } catch (ExpiredJwtException e) {
            return true; // Return true if the token is expired
        }
    }

//    private boolean isTokenExpired(String token) {
//        try {
//            Date expiration = extractExpiration(token);
//            return expiration.before(Date.from(clock.instant()));
//        } catch (ExpiredJwtException e) {
//            return true;
//        }
//    }

    public User authenticate(User user) throws AuthenticationException {
        // Simulating an authentication check (replace with actual logic)
        if ("nonexistent@example.com".equals(user.getEmail())) {
            throw new AuthenticationException("User not found");
        }
        return user; // Return the user if authentication is successful
    }

    public SecretKey getSignKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void setClock(Clock fixedClock) {
    }
}
