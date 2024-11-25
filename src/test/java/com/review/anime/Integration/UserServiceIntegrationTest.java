package com.review.anime.Integration;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import com.review.anime.repository.UserRepository;
import com.review.anime.security.JwtService;
import com.review.anime.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test") // Use a test profile to load specific configurations
@Transactional // Ensure database rolls back after each test
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.USER);
        testUser.setWatchLists(new ArrayList<>());
        userRepository.save(testUser);
    }




//    @Test
//    void testAuthenticateSuccess() throws AuthenticationException {
//        User loginRequest = new User();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        // Generate the token during authentication
//        String token = userService.authenticate(loginRequest);
//
//        // Validate the token using JwtService's isValid method
//        boolean isValid = jwtService.isValid(token, loginRequest);
//
//        // Assertions to verify the token's validity
//        assertThat(token).isNotBlank();
//        assertThat(isValid).isTrue();
//    }


//    @Test
//    void testAuthenticateUser_Success() throws AuthenticationException {
//        User mockUser = new User();
//        mockUser.setEmail("test@domain.com");
//        mockUser.setPassword("password");
//
//        when(userRepository.findByEmail("test@domain.com")).thenReturn(mockUser);
//        when(jwtService.generateToken(mockUser)).thenReturn("validToken");
//
//        String token = userService.authenticate(mockUser);
//        assertEquals("validToken", token);
//    }
//
//    @Test
//    void testAuthenticateUser_Failure_UserNotFound() {
//        User mockUser = new User();
//        mockUser.setEmail("test@domain.com");
//        mockUser.setPassword("password");
//
//        when(userRepository.findByEmail("test@domain.com")).thenReturn(null);
//
//        assertThrows(AuthenticationException.class, () -> {
//            userService.authenticate(mockUser);
//        });
//    }

    @Test
    void testAddUserSuccess() {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpassword");
        newUser.setRole(Role.USER);

        userService.addUser(newUser);

        User savedUser = userRepository.findByEmail("newuser@example.com");
        assertThat(savedUser).isNotNull();
        assertThat(passwordEncoder.matches("newpassword", savedUser.getPassword())).isTrue();
    }

    @Test
    void testAddUserFailureForDuplicateEmail() {
        User duplicateUser = new User();
        duplicateUser.setEmail("test@example.com");
        duplicateUser.setPassword("newpassword");

        assertThatThrownBy(() -> userService.addUser(duplicateUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with this email already exists");
    }

    @Test
    void testAddWatchedAnimeIdSuccess() {
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setTitle("Naruto");
        watchList.setImageUrl("http://example.com/naruto.jpg");

        String result = userService.addWatchedAnimeId(watchList, "test@example.com");

        assertThat(result).isEqualTo("Anime added to watch list.");
        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(updatedUser.getWatchLists()).hasSize(1);
        assertThat(updatedUser.getWatchLists().get(0).getTitle()).isEqualTo("Naruto");
    }

    @Test
    void testAddWatchedAnimeIdAlreadyExists() {
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setTitle("Naruto");
        watchList.setImageUrl("http://example.com/naruto.jpg");

        // Add once
        userService.addWatchedAnimeId(watchList, "test@example.com");

        // Add again
        String result = userService.addWatchedAnimeId(watchList, "test@example.com");

        assertThat(result).isEqualTo("Anime already in watch list.");
    }

    @Test
    void testDeleteWatchListSuccess() {
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setTitle("Naruto");
        watchList.setImageUrl("http://example.com/naruto.jpg");

        // Add to watchlist
        userService.addWatchedAnimeId(watchList, "test@example.com");

        // Delete from watchlist
        String result = userService.deleteWatchList(1, testUser);

        assertThat(result).isEqualTo("Anime removed from watch list successfully.");
        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(updatedUser.getWatchLists()).isEmpty();
    }

    @Test
    void testDeleteWatchListNotFound() {
        String result = userService.deleteWatchList(999, testUser);

        assertThat(result).isEqualTo("Anime not found in watch list.");
    }

    @Test
    void testVerifyCurrentPasswordSuccess() {
        boolean result = userService.verifyCurrentPassword("test@example.com", "password");

        assertThat(result).isTrue();
    }

    @Test
    void testVerifyCurrentPasswordFailure() {
        boolean result = userService.verifyCurrentPassword("test@example.com", "wrongpassword");

        assertThat(result).isFalse();
    }

    @Test
    void testUpdateUserSuccess() {
        userService.updateUser("test@example.com", "newpassword");

        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(passwordEncoder.matches("newpassword", updatedUser.getPassword())).isTrue();
    }

    @Test
    void testUpdateUserNotFound() {
        assertThatThrownBy(() -> userService.updateUser("nonexistent@example.com", "newpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

//    @Test
//    void testGetAdminList() {
//        User adminUser = new User();
//        adminUser.setEmail("admin@example.com");
//        adminUser.setPassword(passwordEncoder.encode("adminpassword"));
//        adminUser.setRole(Role.ADMIN);
//        userRepository.save(adminUser);
//
//        List<User> admins = userService.getAdminList();
//
//        assertThat(admins).hasSize(1); // Check if there is only one admin user
//        assertThat(admins.get(0).getEmail()).isEqualTo("admin@example.com");
//    }

}
