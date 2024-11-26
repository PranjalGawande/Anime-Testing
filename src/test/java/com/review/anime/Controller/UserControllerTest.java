package com.review.anime.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.review.anime.dto.ExtraDTO;
import com.review.anime.dto.ReviewDTO;
import com.review.anime.dto.Token;
import com.review.anime.entites.Review;
import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import com.review.anime.service.ReviewService;
import com.review.anime.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private Review testReview;
    private WatchList testWatchList;
    private ExtraDTO testExtraDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);

        testReview = new Review();
        testReview.setComment("Test comment");
        testReview.setRating(5.0f);
        testReview.setAnimeId(123);
        testReview.setUser(testUser);

        testWatchList = new WatchList();
        testWatchList.setAnimeId(123);
        testWatchList.setImageUrl("test-url");
        testWatchList.setTitle("Test Anime");

        testExtraDTO = new ExtraDTO();
        testExtraDTO.setAnimeId(123);
        testExtraDTO.setComment("Test comment");
        testExtraDTO.setRating(5.4f);
    }

    // Authentication Tests
    @Test
    void loginSuccess() throws AuthenticationException {
        when(userService.authenticate(any(User.class))).thenReturn("test-token");

        ResponseEntity<Token> response = userController.userLogin(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-token", response.getBody().getToken());
    }

    @Test
    void loginFailed() throws AuthenticationException {
        when(userService.authenticate(any(User.class))).thenThrow(new AuthenticationException("Invalid credentials"));

        ResponseEntity<Token> response = userController.userLogin(testUser);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Registration Tests
    @Test
    void registerNewUser() {
        when(userService.findUserByEmail(anyString())).thenReturn(null);

        ResponseEntity<String> response = userController.userRegister(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registered Successfully.", response.getBody());
        verify(userService).addUser(testUser);
    }

    @Test
    void registerExistingUser() {
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        ResponseEntity<String> response = userController.userRegister(testUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username/Email is already taken.", response.getBody());
    }

    @Test
    void registerAsAdmin() {
        testUser.setRole(Role.ADMIN);

        ResponseEntity<String> response = userController.userRegister(testUser);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Cannot register as Admin.", response.getBody());
    }

    // Review Tests
    @Test
    void addValidComment() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        ResponseEntity<String> response = userController.addComment(testExtraDTO, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully added Comment", response.getBody());
        verify(reviewService).saveReview(any(Review.class));
    }

    @Test
    void addInvalidComment() {
        ExtraDTO invalidDTO = new ExtraDTO();
        invalidDTO.setAnimeId(-1);
        invalidDTO.setRating(-1.0f);

        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        ResponseEntity<String> response = userController.addComment(invalidDTO, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid comment data provided", response.getBody());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void getComments() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewOfAnimeId(anyInt())).thenReturn(reviews);

        ResponseEntity<List<ReviewDTO>> response = userController.getComment(123);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    // Watchlist Tests
    @Test
    void getWatchlistWithData() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        List<WatchList> watchLists = Arrays.asList(testWatchList);
        testUser.setWatchLists(watchLists);

        ObjectNode watchListJson = mock(ObjectNode.class);
        ArrayNode dataArray = mock(ArrayNode.class);
        ObjectNode animeObject = mock(ObjectNode.class);
        ObjectNode imagesObject = mock(ObjectNode.class);

        when(objectMapper.createObjectNode()).thenReturn(watchListJson, animeObject, imagesObject);
        when(objectMapper.createArrayNode()).thenReturn(dataArray);
        when(watchListJson.set(eq("data"), any(ArrayNode.class))).thenReturn(watchListJson);

        ResponseEntity<Object> response = userController.getWatchList(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getEmptyWatchlist() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        testUser.setWatchLists(new ArrayList<>());

        ResponseEntity<Object> response = userController.getWatchList(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No watchlist data found", response.getBody());
    }

    @Test
    void addToWatchlist() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.addWatchedAnimeId(any(WatchList.class), anyString())).thenReturn("Added to watchlist");

        ResponseEntity<String> response = userController.addWatchList(testWatchList, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added to watchlist", response.getBody());
    }

    @Test
    void deleteFromWatchlist() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        when(userService.deleteWatchList(anyInt(), any(User.class))).thenReturn("Deleted from watchlist");

        ResponseEntity<String> response = userController.deleteWatchList(testExtraDTO, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted from watchlist", response.getBody());
    }

    // Password Management Tests
    @Test
    void changePasswordSuccess() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        when(userService.verifyCurrentPassword(anyString(), anyString())).thenReturn(true);

        testExtraDTO.setOldPassword("oldPassword");
        testExtraDTO.setNewPassword("newPassword");

        ResponseEntity<String> response = userController.changePassword(testExtraDTO, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully.", response.getBody());
        verify(userService).updateUser(anyString(), anyString());
    }

    @Test
    void changePasswordInvalidOld() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        when(userService.verifyCurrentPassword(anyString(), anyString())).thenReturn(false);

        ResponseEntity<String> response = userController.changePassword(testExtraDTO, userDetails);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Current password is incorrect", response.getBody());
    }

    // Admin Operations Tests
    @Test
    void adminDeleteComment() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.ADMIN);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        ResponseEntity<String> response = userController.deleteComment(1, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully.", response.getBody());
        verify(reviewService).deleteComment(1);
    }

    @Test
    void nonAdminDeleteComment() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.USER);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        ResponseEntity<String> response = userController.deleteComment(1, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only Admin is allowed to delete comment.", response.getBody());
        verify(reviewService, never()).deleteComment(anyInt());
    }

    @Test
    void deleteNonExistentComment() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.ADMIN);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        doThrow(new IllegalArgumentException("Comment not found")).when(reviewService).deleteComment(anyInt());

        ResponseEntity<String> response = userController.deleteComment(999, userDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Comment not found", response.getBody());
    }
}