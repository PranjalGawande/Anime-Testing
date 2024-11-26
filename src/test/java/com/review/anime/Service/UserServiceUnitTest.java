package com.review.anime.Service;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import com.review.anime.repository.UserRepository;
import com.review.anime.security.JwtService;
import com.review.anime.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserServiceUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Authentication Tests
    @Test
    void testAuthSuccess() throws AuthenticationException {
        User mockUser = new User();
        mockUser.setEmail("test@domain.com");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("test@domain.com")).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("validToken");

        String token = userService.authenticate(mockUser);
        assertEquals("validToken", token);
    }

    @Test
    void testAuthFailure() {
        User mockUser = new User();
        mockUser.setEmail("test@domain.com");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("test@domain.com")).thenReturn(null);

        assertThrows(AuthenticationException.class, () -> {
            userService.authenticate(mockUser);
        });
    }

    // User Management Tests
    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("newpassword");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedpassword");

        userService.addUser(user);

        verify(passwordEncoder).encode("newpassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("encodedpassword", savedUser.getPassword());
    }

    @Test
    void testAddUserExistingEmail() {
        User user = new User();
        user.setEmail("existinguser@example.com");
        user.setPassword("password");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(user);
        });

        assertTrue(exception.getMessage().contains("User with this email already exists"));
        verify(userRepository, times(0)).save(user);
    }

    // Password Management Tests
    @Test
    void testVerifyPasswordSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("oldpassword", user.getPassword())).thenReturn(true);

        boolean result = userService.verifyCurrentPassword(user.getEmail(), "oldpassword");

        assertTrue(result);
        verify(passwordEncoder, times(1)).matches(any(), any());
    }

    @Test
    void testVerifyPasswordFailure() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        boolean result = userService.verifyCurrentPassword(user.getEmail(), "wrongpassword");

        assertFalse(result);
        verify(passwordEncoder, times(1)).matches(any(), any());
    }

    @Test
    void testUpdateUserSuccess() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        userService.updateUser(user.getEmail(), "newpassword");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User();
        user.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user.getEmail(), "newpassword");
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

    // WatchList Management Tests
    @Test
    void testAddToWatchList() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>());

        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());

        assertEquals("Anime added to watch list.", result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getWatchLists());
        assertEquals(1, savedUser.getWatchLists().size());

        WatchList addedWatchList = savedUser.getWatchLists().get(0);
        assertEquals(1, addedWatchList.getAnimeId());
        assertEquals("http://example.com/image.jpg", addedWatchList.getImageUrl());
        assertEquals("Test Anime", addedWatchList.getTitle());
        assertEquals(user, addedWatchList.getUser());
    }

    @Test
    void testAddToWatchListDuplicate() {
        User user = new User();
        user.setEmail("test@example.com");
        List<WatchList> watchLists = new ArrayList<>();
        WatchList existingWatchList = new WatchList();
        existingWatchList.setAnimeId(1);
        watchLists.add(existingWatchList);
        user.setWatchLists(watchLists);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());
        assertEquals("Anime already in watch list.", result);
    }

    @Test
    void testDeleteWatchListSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>());
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);

        user.getWatchLists().add(watchList);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.deleteWatchList(1, user);

        assertEquals("Anime removed from watch list successfully.", result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteWatchListNotFound() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.deleteWatchList(1, user);

        assertEquals("Anime not found in watch list.", result);
        verify(userRepository, times(0)).save(user);
    }

    // Edge Cases and Error Handling Tests
    @Test
    void testAddToWatchListNullWatchList() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                userService.addWatchedAnimeId(null, "test@example.com")
        );
        assertEquals("WatchList cannot be null", exception.getMessage());
    }

    @Test
    void testAddToWatchListInvalidEmail() {
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addWatchedAnimeId(watchList, "")
        );
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddToWatchListUserNotFound() {
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.addWatchedAnimeId(watchList, "nonexistent@example.com")
        );
        assertEquals("User not found", exception.getMessage());
    }
}