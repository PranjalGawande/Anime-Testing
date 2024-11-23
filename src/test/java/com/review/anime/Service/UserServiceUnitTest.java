package com.review.anime.Service;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import com.review.anime.repository.UserRepository;
import com.review.anime.security.JwtService;
import com.review.anime.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testAuthenticateSuccess() throws AuthenticationException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt_token");

        String token = userService.authenticate(user);

        assertEquals("jwt_token", token);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

//    @Test
//    void testAuthenticateFailure() {
//        User user = new User();
//        user.setEmail("invalid@example.com");
//        user.setPassword("password");
//
//        // Mocking the userRepository to return null (user not found)
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
//
//        // Perform the test and assert that the exception is thrown
//        Exception exception = assertThrows(AuthenticationException.class, () -> {
//            userService.authenticate(user);
//        });
//
//        // Assert that the exception message contains the expected message
//        assertTrue(exception.getMessage().contains("Authentication failed: User not found."));
//
//        // Verify that the authentication manager was not called
//        verify(authenticationManager, times(0)).authenticate(any());
//    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("newpassword");

        // Mocking the userRepository to return null (user not found)
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Mocking the password encoder to return the encoded password
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedpassword");

        // Perform the test
        userService.addUser(user);

        // Verify the password encoding and that save was called
        verify(passwordEncoder).encode("newpassword");  // Verify the correct password was encoded
        verify(userRepository).save(user);  // Verify that the user was saved
    }

    @Test
    void testAddUserWithExistingEmail() {
        User user = new User();
        user.setEmail("existinguser@example.com");
        user.setPassword("password");

        // Mock existsByEmail to return true
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Perform the test
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(user);
        });

        // Assert that the exception message is as expected
        assertTrue(exception.getMessage().contains("User with this email already exists"));

        // Ensure save() is not called
        verify(userRepository, times(0)).save(user);
    }


    @Test
    void testAddWatchedAnimeIdSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>()); // Initialize the watch list
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());

        assertEquals("Anime added to watch list.", result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddWatchedAnimeIdAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>()); // Initialize the watch list
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);

        WatchList existingWatchList = new WatchList();
        existingWatchList.setAnimeId(1);
        user.getWatchLists().add(existingWatchList);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());

        assertEquals("Anime already in watch list.", result);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testDeleteWatchListSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>()); // Initialize the watch list
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
        user.setWatchLists(new ArrayList<>()); // Initialize the watch list

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.deleteWatchList(1, user);

        assertEquals("Anime not found in watch list.", result);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testVerifyCurrentPasswordSuccess() {
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
    void testVerifyCurrentPasswordFailure() {
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

        // Mocking the userRepository to return null (user not found)
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        // Perform the test and assert that the exception is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user.getEmail(), "newpassword");
        });

        // Assert that the exception message contains the expected message
        assertTrue(exception.getMessage().contains("User not found"));
    }

}
