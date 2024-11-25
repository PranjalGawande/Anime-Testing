package com.review.anime.Service;

import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import com.review.anime.repository.WatchListRepository;
import com.review.anime.service.WatchListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchListServiceUnitTest {

    @Mock
    private WatchListRepository watchListRepository;

    @InjectMocks
    private WatchListService watchListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddWatchAnime_Success() {
        // Given
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", user);

        // When & Then
        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_Exception() {
        // Given
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", user);

        // Mock repository to throw an exception
        doThrow(new RuntimeException("Database error")).when(watchListRepository).save(watchList);

        // When & Then
        assertThrows(RuntimeException.class, () -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_NullWatchList() {
        // Given
        WatchList watchList = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_NullAnimeId() {
        // Given
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, null, "https://image.url/anime.jpg", "Naruto", user);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_EmptyFields() {
        // Given
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 0, "", "", user);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_SuccessWithNonNullFields() {
        // Given
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 200, "https://image.url/anotheranime.jpg", "Attack on Titan", user);

        // When & Then
        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_InvalidUser() {
        // Given
        User invalidUser = null; // Simulate an invalid user (null user)
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", invalidUser);

        // When & Then
        assertThrows(NullPointerException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_ValidAnimeId() {
        // Given
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", user);

        // When & Then
        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_ValidURL() {
        // Givenx
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://valid.url/to/anime.jpg", "Naruto", user);

        // When & Then
        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

//    @Test
//    void testAddWatchAnime_EmptyURL() {
//        // Given
//        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
//        WatchList watchList = new WatchList(null, 101, "", "Naruto", user);
//
//        // When & Then
//        assertThrows(IllegalArgumentException.class, () -> watchListService.addWatchAnime(watchList));
//    }
}
