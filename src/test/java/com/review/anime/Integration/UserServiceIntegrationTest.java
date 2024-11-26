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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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

    // User Registration Tests
    @Test
    void testAddUser() {
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
    void testAddUserDuplicate() {
        User duplicateUser = new User();
        duplicateUser.setEmail("test@example.com");
        duplicateUser.setPassword("newpassword");

        assertThatThrownBy(() -> userService.addUser(duplicateUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with this email already exists");
    }

    // Password Management Tests
    @Test
    void testVerifyPassword() {
        boolean result = userService.verifyCurrentPassword("test@example.com", "password");
        assertThat(result).isTrue();
    }

    @Test
    void testVerifyWrongPassword() {
        boolean result = userService.verifyCurrentPassword("test@example.com", "wrongpassword");
        assertThat(result).isFalse();
    }

    @Test
    void testUpdatePassword() {
        userService.updateUser("test@example.com", "newpassword");

        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(passwordEncoder.matches("newpassword", updatedUser.getPassword())).isTrue();
    }

    @Test
    void testUpdateNonexistentUser() {
        assertThatThrownBy(() -> userService.updateUser("nonexistent@example.com", "newpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // Watchlist Management Tests
    @Test
    void testAddAnime() {
        WatchList watchList = createTestWatchlist();
        String result = userService.addWatchedAnimeId(watchList, "test@example.com");

        assertThat(result).isEqualTo("Anime added to watch list.");
        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(updatedUser.getWatchLists()).hasSize(1);
        assertThat(updatedUser.getWatchLists().get(0).getTitle()).isEqualTo("Naruto");
    }

    @Test
    void testAddDuplicateAnime() {
        WatchList watchList = createTestWatchlist();
        userService.addWatchedAnimeId(watchList, "test@example.com");

        String result = userService.addWatchedAnimeId(watchList, "test@example.com");
        assertThat(result).isEqualTo("Anime already in watch list.");
    }

    @Test
    void testDeleteAnime() {
        WatchList watchList = createTestWatchlist();
        userService.addWatchedAnimeId(watchList, "test@example.com");

        String result = userService.deleteWatchList(1, testUser);

        assertThat(result).isEqualTo("Anime removed from watch list successfully.");
        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(updatedUser.getWatchLists()).isEmpty();
    }

    @Test
    void testDeleteNonexistentAnime() {
        String result = userService.deleteWatchList(999, testUser);
        assertThat(result).isEqualTo("Anime not found in watch list.");
    }

    private WatchList createTestWatchlist() {
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setTitle("Naruto");
        watchList.setImageUrl("http://example.com/naruto.jpg");
        return watchList;
    }
}