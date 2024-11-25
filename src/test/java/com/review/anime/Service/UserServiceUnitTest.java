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

    @Test
    void testAuthenticateUser_Success() throws AuthenticationException {
        User mockUser = new User();
        mockUser.setEmail("test@domain.com");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("test@domain.com")).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("validToken");

        String token = userService.authenticate(mockUser);
        assertEquals("validToken", token);
    }

    @Test
    void testAuthenticateUser_Failure_UserNotFound() {
        User mockUser = new User();
        mockUser.setEmail("test@domain.com");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("test@domain.com")).thenReturn(null);

        assertThrows(AuthenticationException.class, () -> {
            userService.authenticate(mockUser);
        });
    }

//    @Test
//    void testAddUser_EmailExists() {
//        User mockUser = new User();
//        mockUser.setEmail("existing@domain.com");
//        mockUser.setPassword("password");
//
//        when(userRepository.existsByEmail("existing@domain.com")).thenReturn(true);
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            userService.addUser(mockUser);
//        });
//    }

    @Test
    void testAddUser() {
        // Create a mock User object
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("newpassword");

        // Mock behavior for repository and password encoder
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false); // User does not exist
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedpassword"); // Encoded password

        // Perform the operation
        userService.addUser(user);

        // Verify the password encoder was called with the correct argument
        verify(passwordEncoder).encode("newpassword");

        // Verify that the UserRepository save method was called
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        // Assert that the password was set to the encoded password
        User savedUser = userCaptor.getValue();
        assertEquals("encodedpassword", savedUser.getPassword(), "Password should be encoded before saving");
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
        // Mock user
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>()); // Initialize the watch list

        // Mock watch list to be added
        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        // Stub repository behavior
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // Perform the operation
        String result = userService.addWatchedAnimeId(watchList, user.getEmail());

        // Verify expected outcome
        assertEquals("Anime added to watch list.", result);

        // Capture the argument passed to userRepository.save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        // Extract and inspect the updated user object
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getWatchLists());
        assertEquals(1, savedUser.getWatchLists().size());

        // Extract and inspect the added WatchList object
        WatchList addedWatchList = savedUser.getWatchLists().get(0);
        assertEquals(1, addedWatchList.getAnimeId());
        assertEquals("http://example.com/image.jpg", addedWatchList.getImageUrl());
        assertEquals("Test Anime", addedWatchList.getTitle());
        assertEquals(user, addedWatchList.getUser());
    }


//    @Test
//    void testAddWatchedAnimeIdAlreadyExists() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setWatchLists(new ArrayList<>()); // Initialize the watch list
//        WatchList watchList = new WatchList();
//        watchList.setAnimeId(1);
//
//        WatchList existingWatchList = new WatchList();
//        existingWatchList.setAnimeId(1);
//        user.getWatchLists().add(existingWatchList);
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
//
//        String result = userService.addWatchedAnimeId(watchList, user.getEmail());
//
//        assertEquals("Anime already in watch list.", result);
//        verify(userRepository, times(0)).save(user);
//    }

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

//    @Test
//    void testAddWatchedAnimeIdUserNotFound() {
//        WatchList watchList = new WatchList();
//        watchList.setAnimeId(1);
//
//        // Mock `findUserByEmail` to return null
//        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);
//
//        String result = userService.addWatchedAnimeId(watchList, "nonexistent@example.com");
//
//        assertEquals("User not Found", result);
//        verify(userRepository, times(0)).save(any());
//    }

    @Test
    void testAddWatchedAnimeIdException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>());

        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);

        // Mock `userRepository.findByEmail` to throw an exception
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.addWatchedAnimeId(watchList, user.getEmail())
        );

        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testAddWatchedAnimeIdWithInvalidWatchList() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // Test with null `WatchList`
        Exception exception = assertThrows(NullPointerException.class, () ->
                userService.addWatchedAnimeId(null, user.getEmail())
        );

        assertNotNull(exception);

        // Test with missing `animeId`
        WatchList watchList = new WatchList();
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        exception = assertThrows(NullPointerException.class, () ->
                userService.addWatchedAnimeId(watchList, user.getEmail())
        );

        assertNotNull(exception);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testAddWatchedAnimeIdWithEmptyWatchList() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(null); // No watch list initialized

        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());

        assertEquals("Anime added to watch list.", result);
        verify(userRepository, times(1)).save(user);
    }

//    @Test
//    void testAddWatchedAnimeIdWithNullWatchList() {
//        assertThrows(NullPointerException.class, () ->
//                userService.addWatchedAnimeId(null, "test@example.com")
//        );
//    }

    @Test
    void testAddWatchedAnimeIdWithNullWatchLists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(null); // Simulate uninitialized watchLists

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());
        assertEquals("Anime added to watch list.", result);
        assertNotNull(user.getWatchLists());
        assertEquals(1, user.getWatchLists().size());
    }

    @Test
    void testAddWatchedAnimeIdAlreadyExists() {
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
    void testAddWatchedAnimeIdWithNullWatchList() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                userService.addWatchedAnimeId(null, "test@example.com")
        );
        assertEquals("WatchList cannot be null", exception.getMessage());
    }

    @Test
    void testAddWatchedAnimeIdWithInvalidEmail() {
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
    void testAddWatchedAnimeIdUserNotFound() {
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

    @Test
    void testAddWatchedAnimeIdValid() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setWatchLists(new ArrayList<>());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        WatchList watchList = new WatchList();
        watchList.setAnimeId(1);
        watchList.setImageUrl("http://example.com/image.jpg");
        watchList.setTitle("Test Anime");

        String result = userService.addWatchedAnimeId(watchList, user.getEmail());
        assertEquals("Anime added to watch list.", result);
    }

}
