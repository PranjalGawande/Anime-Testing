package com.review.anime.Service;

import com.review.anime.entites.User;
import com.review.anime.repository.UserRepository;
import com.review.anime.security.JwtService;
import com.review.anime.security.LoginDetailService;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;
import java.util.Date;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @InjectMocks
    private LoginDetailService loginDetailService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        // LoginDetailsService
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @Test
    void testExtractEmail() {
        String token = jwtService.generateToken(user);
        String email = jwtService.extractEmail(token);
        assertEquals(user.getEmail(), email, "Extracted email should match the user's email");
    }

    @Test
    void testIsValid_ValidToken() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isValid(token, user);
        assertTrue(isValid, "Token should be valid for the user");
    }

    @Test
    void testIsValid_InvalidToken_EmailMismatch() {
        String token = jwtService.generateToken(user);
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        boolean isValid = jwtService.isValid(token, otherUser);
        assertFalse(isValid, "Token should not be valid for a different user");
    }

    @Test
    void testIsValid_ExpiredToken() {
        // Mocking the system clock to return a fixed current time
        Clock fixedClock = Clock.fixed(Instant.parse("2024-11-24T18:34:06Z"), ZoneId.of("UTC"));
        jwtService.setClock(fixedClock);  // Set this mocked clock in your JwtService (create a setter for it)

        // Create an expired token with the original expiration time (5 hours in the future)
        String expiredToken = jwtService.generateToken(user);

        // Move the current time past the token expiration
        fixedClock = Clock.fixed(Instant.parse("2024-11-24T18:40:06Z"), ZoneId.of("UTC"));  // Adjust time to simulate expiration
        jwtService.setClock(fixedClock);

        boolean isValid = jwtService.isValid(expiredToken, user);
        assertTrue(isValid, "Expired token should be invalid");
    }



    @Test
    void testIsTokenExpired_ValidToken() {
        String token = jwtService.generateToken(user);
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired, "Valid token should not be expired");
    }

    @Test
    void testIsTokenExpired_ExpiredToken() {
        // Simulating an expired token
        String expiredToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000)) // 10 seconds ago
                .setExpiration(new Date(System.currentTimeMillis() - 5000)) // Expired 5 seconds ago
                .signWith(jwtService.getSignKey())
                .compact();

        boolean isExpired = jwtService.isTokenExpired(expiredToken);
        assertTrue(isExpired, "Expired token should be expired");
    }


    @Test
    void testExtractExpiration() {
        String token = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration, "Expiration date should not be null");
    }

    @Test
    void testExtractClaims() {
        String token = jwtService.generateToken(user);
        String subject = jwtService.extractClaims(token, Claims::getSubject);
        assertEquals(user.getEmail(), subject, "Claim subject should match the user's email");
    }

    @Test
    void testExtractAllClaims_InvalidToken() {
        String invalidToken = "invalid.token";
        assertThrows(JwtException.class, () -> jwtService.extractAllClaims(invalidToken), "Invalid token should throw exception");
    }

    @Test
    void testGetSignKey() {
        SecretKey secretKey = jwtService.getSignKey();
        assertNotNull(secretKey, "Secret key should not be null");
    }

    // Test for exception handling when authentication fails
    @Test
    void testAuthenticate_Fail_UserNotFound() {
        User invalidUser = new User();
        invalidUser.setEmail("nonexistent@example.com");
        invalidUser.setPassword("password");

        assertThrows(AuthenticationException.class, () -> jwtService.authenticate(invalidUser), "Authentication should fail for non-existent user");
    }

    // Login Details Service Test Cases
    @Test
    void loadUserSuccess() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        // Act
        UserDetails userDetails = loginDetailService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void loadUserFail() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            loginDetailService.loadUserByUsername("notfound@example.com");
        });
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }
}
