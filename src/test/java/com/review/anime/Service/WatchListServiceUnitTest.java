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
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", user);

        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_Exception() {
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", user);

        doThrow(new RuntimeException("Database error")).when(watchListRepository).save(watchList);

        assertThrows(RuntimeException.class, () -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_NullWatchList() {
        WatchList watchList = null;

        assertThrows(NullPointerException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_NullAnimeId() {
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, null, "https://image.url/anime.jpg", "Naruto", user);

        assertThrows(IllegalArgumentException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_EmptyFields() {
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 0, "", "", user);

        assertThrows(IllegalArgumentException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_SuccessWithNonNullFields() {
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 200, "https://image.url/anotheranime.jpg", "Attack on Titan", user);

        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_InvalidUser() {
        User invalidUser = null;
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", invalidUser);

        assertThrows(NullPointerException.class, () -> watchListService.addWatchAnime(watchList));
    }

    @Test
    void testAddWatchAnime_ValidAnimeId() {
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://image.url/anime.jpg", "Naruto", user);

        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }

    @Test
    void testAddWatchAnime_ValidURL() {
        User user = new User(1, "User1", "user1@test.com", "password", null, true, null, null);
        WatchList watchList = new WatchList(null, 101, "https://valid.url/to/anime.jpg", "Naruto", user);

        assertDoesNotThrow(() -> watchListService.addWatchAnime(watchList));
        verify(watchListRepository, times(1)).save(watchList);
    }
}
