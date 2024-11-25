package com.review.anime.Service;

import com.review.anime.entites.User;
import com.review.anime.security.JwtAuthFilter;
import com.review.anime.security.JwtService;
import com.review.anime.security.LoginDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private LoginDetailService loginDetailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    // Create a test-specific subclass
    private static class TestableJwtAuthFilter extends JwtAuthFilter {
        @Override
        public void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {
            super.doFilterInternal(request, response, filterChain);
        }
    }

    private TestableJwtAuthFilter jwtAuthFilter;

    private static final String VALID_TOKEN = "valid_token";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        jwtAuthFilter = new TestableJwtAuthFilter();
        // Manually inject the mocked dependencies
        ReflectionTestUtils.setField(jwtAuthFilter, "jwtService", jwtService);
        ReflectionTestUtils.setField(jwtAuthFilter, "loginDetailService", loginDetailService);
    }

    @Test
    void testNullHeader() throws Exception {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractEmail(anyString());
    }

    @Test
    void testInvalidHeader() throws Exception {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Invalid " + VALID_TOKEN);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractEmail(anyString());
    }

    @Test
    void testNullEmail() throws Exception {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractEmail(VALID_TOKEN)).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(loginDetailService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testAlreadyAuthenticated() throws Exception {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractEmail(VALID_TOKEN)).thenReturn(VALID_EMAIL);
        when(securityContext.getAuthentication()).thenReturn(mock(UsernamePasswordAuthenticationToken.class));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(loginDetailService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testInvalidToken() throws Exception {
        User userDetails = new User();
        userDetails.setEmail(VALID_EMAIL);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractEmail(VALID_TOKEN)).thenReturn(VALID_EMAIL);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(loginDetailService.loadUserByUsername(VALID_EMAIL)).thenReturn(userDetails);
        when(jwtService.isValid(VALID_TOKEN, userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testValidToken() throws Exception {
        User userDetails = new User();
        userDetails.setEmail(VALID_EMAIL);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractEmail(VALID_TOKEN)).thenReturn(VALID_EMAIL);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(loginDetailService.loadUserByUsername(VALID_EMAIL)).thenReturn(userDetails);
        when(jwtService.isValid(VALID_TOKEN, userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    }
}