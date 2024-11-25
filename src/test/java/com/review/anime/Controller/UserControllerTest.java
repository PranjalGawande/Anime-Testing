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
        // Setup test user
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);

        // Setup test review
        testReview = new Review();
//        testReview.setUserId(1);
        testReview.setComment("Test comment");
        testReview.setRating(5.0f);
        testReview.setAnimeId(123);
        testReview.setUser(testUser);

        // Setup test watchlist
        testWatchList = new WatchList();
        testWatchList.setAnimeId(123);
        testWatchList.setImageUrl("test-url");
        testWatchList.setTitle("Test Anime");

        // Setup test ExtraDTO
        testExtraDTO = new ExtraDTO();
        testExtraDTO.setAnimeId(123);
        testExtraDTO.setComment("Test comment");
        testExtraDTO.setRating(5.4f);
    }

    @Test
    void userLogin_Success() throws AuthenticationException {
        // Arrange
        when(userService.authenticate(any(User.class))).thenReturn("test-token");

        // Act
        ResponseEntity<Token> response = userController.userLogin(testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-token", response.getBody().getToken());
    }

    @Test
    void userLogin_Failed() throws AuthenticationException {
        // Arrange
        when(userService.authenticate(any(User.class))).thenThrow(new AuthenticationException("Invalid credentials"));

        // Act
        ResponseEntity<Token> response = userController.userLogin(testUser);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void userRegister_Success() {
        // Arrange
        when(userService.findUserByEmail(anyString())).thenReturn(null);

        // Act
        ResponseEntity<String> response = userController.userRegister(testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registered Successfully.", response.getBody());
        verify(userService).addUser(testUser);
    }

    @Test
    void userRegister_ExistingUser() {
        // Arrange
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Act
        ResponseEntity<String> response = userController.userRegister(testUser);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username/Email is already taken.", response.getBody());
    }

    @Test
    void userRegister_AdminRole() {
        // Arrange
        testUser.setRole(Role.ADMIN);

        // Act
        ResponseEntity<String> response = userController.userRegister(testUser);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Cannot register as Admin.", response.getBody());
    }

    @Test
    void addComment_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Act
        ResponseEntity<String> response = userController.addComment(testExtraDTO, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully added Comment", response.getBody());
        verify(reviewService).saveReview(any(Review.class));
    }

    @Test
    void getComment_Success() {
        // Arrange
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewOfAnimeId(anyInt())).thenReturn(reviews);

        // Act
        ResponseEntity<List<ReviewDTO>> response = userController.getComment(123);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getWatchList_Success() {
        // Arrange
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

        // Act
        ResponseEntity<Object> response = userController.getWatchList(userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void addWatchList_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.addWatchedAnimeId(any(WatchList.class), anyString())).thenReturn("Added to watchlist");

        // Act
        ResponseEntity<String> response = userController.addWatchList(testWatchList, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added to watchlist", response.getBody());
    }

    @Test
    void deleteWatchList_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        when(userService.deleteWatchList(anyInt(), any(User.class))).thenReturn("Deleted from watchlist");

        // Act
        ResponseEntity<String> response = userController.deleteWatchList(testExtraDTO, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted from watchlist", response.getBody());
    }

    @Test
    void changePassword_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        when(userService.verifyCurrentPassword(anyString(), anyString())).thenReturn(true);

        testExtraDTO.setOldPassword("oldPassword");
        testExtraDTO.setNewPassword("newPassword");

        // Act
        ResponseEntity<String> response = userController.changePassword(testExtraDTO, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully.", response.getBody());
        verify(userService).updateUser(anyString(), anyString());
    }

    @Test
    void changePassword_IncorrectOldPassword() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);
        when(userService.verifyCurrentPassword(anyString(), anyString())).thenReturn(false);

        // Act
        ResponseEntity<String> response = userController.changePassword(testExtraDTO, userDetails);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Current password is incorrect", response.getBody());
    }

    @Test
    void deleteComment_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.ADMIN);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Act
        ResponseEntity<String> response = userController.deleteComment(1, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully.", response.getBody());
        verify(reviewService).deleteComment(1);
    }

    @Test
    void deleteComment_NonAdmin() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.USER);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Act
        ResponseEntity<String> response = userController.deleteComment(1, userDetails);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only Admin is allowed to delete comment.", response.getBody());
        verify(reviewService, never()).deleteComment(anyInt());
    }

    @Test
    void addComment_InvalidData() {
        // Arrange
        ExtraDTO invalidExtraDTO = new ExtraDTO();
        invalidExtraDTO.setAnimeId(-1); // Invalid Anime ID
        invalidExtraDTO.setComment(null); // No comment provided
        invalidExtraDTO.setRating(-1.0f); // Invalid rating

        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Act
        ResponseEntity<String> response = userController.addComment(invalidExtraDTO, userDetails);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid comment data provided", response.getBody());
        verify(reviewService, never()).saveReview(any(Review.class));
    }

    @Test
    void getWatchList_EmptyList() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        testUser.setWatchLists(new ArrayList<>()); // Empty watchlist

        // Act
        ResponseEntity<Object> response = userController.getWatchList(userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No watchlist data found", response.getBody());  // This should match the new response
    }


    @Test
    void userLogin_InvalidToken() throws AuthenticationException {
        // Arrange
        when(userService.authenticate(any(User.class))).thenThrow(new AuthenticationException("Invalid credentials"));

        // Act
        ResponseEntity<Token> response = userController.userLogin(testUser);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteComment_CommentDoesNotExist() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.ADMIN);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Simulate that the comment does not exist and an exception is thrown
        doThrow(new IllegalArgumentException("Comment not found")).when(reviewService).deleteComment(anyInt());

        // Act
        ResponseEntity<String> response = userController.deleteComment(999, userDetails); // Non-existing comment ID

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Comment not found", response.getBody());
    }



    @Test
    void deleteComment_InvalidCommentId() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@test.com");
        testUser.setRole(Role.ADMIN);
        when(userService.findUserByEmail(anyString())).thenReturn(testUser);

        // Act
        ResponseEntity<String> response = userController.deleteComment(-1, userDetails); // Invalid comment ID

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid comment ID", response.getBody());
    }

}