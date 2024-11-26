package com.review.anime.Controller;

import com.review.anime.service.AnimeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AnimeControllerTest {

    @Mock
    private AnimeService animeService;

    @InjectMocks
    private AnimeController animeController;

    private static final String MOCK_ANIME_DATA = "{\"data\": []}";
    private static final String TEST_ID = "1234";

    @Test
    void getBackgroundImages_Success() {
        List<String> expectedImages = Arrays.asList("image1.jpg", "image2.jpg");
        when(animeService.getBackgroundImages()).thenReturn(expectedImages);

        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedImages, response.getBody());
    }

    @Test
    void getBackgroundImages_Error() {
        when(animeService.getBackgroundImages()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getBackgroundImages_EmptyList() {
        when(animeService.getBackgroundImages()).thenReturn(Collections.emptyList());

        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getBackgroundImages_NullList() {
        when(animeService.getBackgroundImages()).thenReturn(null);

        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentSeasonAnime_Success() {
        when(animeService.getCurrentSeasonAnime(anyString(), anyInt(), anyInt()))
                .thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getCurrentSeasonAnime("popular", 10, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getCurrentSeasonAnime_Error() {
        when(animeService.getCurrentSeasonAnime(anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getCurrentSeasonAnime("popular", 10, 1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving current season anime"));
    }

    @Test
    void getCurrentSeasonAnime_InvalidPageNumber() {
        int invalidPage = -1;
        when(animeService.getCurrentSeasonAnime(anyString(), anyInt(), eq(invalidPage)))
                .thenThrow(new IllegalArgumentException("Invalid page number"));

        ResponseEntity<String> response = animeController.getCurrentSeasonAnime("popular", 10, invalidPage);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid page number"));
    }

    @Test
    void getUpcomingAnime_Success() {
        when(animeService.getUpcomingAnime(anyInt())).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getUpcomingAnime(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getUpcomingAnime_Error() {
        when(animeService.getUpcomingAnime(anyInt())).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getUpcomingAnime(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving upcoming anime"));
    }

    @Test
    void getTopAnime_Success() {
        when(animeService.getTopAnime(anyInt())).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getTopAnime(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getTopAnime_Error() {
        when(animeService.getTopAnime(anyInt())).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getTopAnime(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving top anime"));
    }

    @Test
    void getTopAnime_NegativePageNumber() {
        int negativePage = -2;
        when(animeService.getTopAnime(negativePage))
                .thenThrow(new IllegalArgumentException("Page number cannot be negative"));

        ResponseEntity<String> response = animeController.getTopAnime(negativePage);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Page number cannot be negative"));
    }

    @Test
    void getTopAnime_InvalidPageNumber() {
        int invalidPage = -1;
        when(animeService.getTopAnime(eq(invalidPage)))
                .thenThrow(new IllegalArgumentException("Invalid page number"));

        ResponseEntity<String> response = animeController.getTopAnime(invalidPage);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid page number"));
    }

    @Test
    void getTopCharacters_Success() {
        when(animeService.getTopCharacters()).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getTopCharacters();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getTopCharacters_Error() {
        when(animeService.getTopCharacters()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getTopCharacters();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving top characters"));
    }

    @Test
    void getTopCharacters_EmptyResponse() {
        when(animeService.getTopCharacters()).thenReturn("");

        ResponseEntity<String> response = animeController.getTopCharacters();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void getRandomAnime_Success() {
        when(animeService.getRandomAnime()).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getRandomAnime();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getRandomAnime_Error() {
        when(animeService.getRandomAnime()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getRandomAnime();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving random anime"));
    }

    @Test
    void getAnimeGenres_Success() {
        when(animeService.getAnimeGenres()).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getAnimeGenres();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeGenres_Error() {
        when(animeService.getAnimeGenres()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getAnimeGenres();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime genres"));
    }

    @Test
    void getAnimeGenres_NullResponse() {
        // Arrange
        when(animeService.getAnimeGenres()).thenReturn(null);

        // Act
        ResponseEntity<String> response = animeController.getAnimeGenres();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void exploreAnimeList_Success() {
        when(animeService.getAnimeExplore(anyInt(), anyString())).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.exploreAnimeList(1, "action,comedy");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void exploreAnimeList_Error() {
        when(animeService.getAnimeExplore(anyInt(), anyString()))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.exploreAnimeList(1, "action,comedy");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime details"));
    }

    @Test
    void exploreAnimeList_InvalidGenre() {
        String invalidGenre = "unknown";
        when(animeService.getAnimeExplore(anyInt(), eq(invalidGenre)))
                .thenThrow(new IllegalArgumentException("Invalid genre"));

        ResponseEntity<String> response = animeController.exploreAnimeList(1, invalidGenre);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid genre"));
    }

    @Test
    void exploreAnimeList_NullResponse() {
        Integer page = 1;
        String genres = "action";

        when(animeService.getAnimeExplore(page, genres)).thenReturn(null);

        ResponseEntity<String> response = animeController.exploreAnimeList(page, genres);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error retrieving anime details: No data available", response.getBody());
    }

    @Test
    void searchAnime_Success() {
        when(animeService.getAnimeSearch(anyString(), anyInt())).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.searchAnime("Naruto", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void searchAnime_Error() {
        when(animeService.getAnimeSearch(anyString(), anyInt()))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.searchAnime("Naruto", 1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error searching anime"));
    }

    @Test
    void searchAnime_EmptyQuery() {
        String emptyQuery = "";
        when(animeService.getAnimeSearch(emptyQuery, 1)).thenThrow(new IllegalArgumentException("Query cannot be empty"));

        ResponseEntity<String> response = animeController.searchAnime(emptyQuery, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Query cannot be empty"));
    }

    @Test
    void getAnimeDetailsById_Success() {
        when(animeService.getAnimeDetailsById(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getAnimeDetailsById(TEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeDetailsById_Error() {
        when(animeService.getAnimeDetailsById(TEST_ID))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getAnimeDetailsById(TEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime details"));
    }

    @Test
    void getAnimeDetailsById_InvalidId() {
        // Arrange
        String invalidId = "invalid";
        when(animeService.getAnimeDetailsById(invalidId))
                .thenThrow(new IllegalArgumentException("Invalid ID"));

        // Act
        ResponseEntity<String> response = animeController.getAnimeDetailsById(invalidId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid ID"));
    }

    @Test
    void getAnimeCharactersList_Success() {
        when(animeService.getAnimeCharactersList(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getAnimeCharactersList(TEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeCharactersList_Error() {
        when(animeService.getAnimeCharactersList(TEST_ID)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getAnimeCharactersList(TEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime characters"));
    }

    @Test
    void getAnimeCharactersList_EmptyResponse() {
        when(animeService.getAnimeCharactersList(TEST_ID)).thenReturn("");

        ResponseEntity<String> response = animeController.getAnimeCharactersList(TEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void getAnimeStaffList_Success() {
        when(animeService.getAnimeStaffList(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getAnimeStaffList(TEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeStaffList_Error() {
        when(animeService.getAnimeStaffList(TEST_ID)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getAnimeStaffList(TEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime staff"));
    }

    @Test
    void getAnimeStatistics_Success() {
        when(animeService.getAnimeStatistics(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getAnimeStatistics(TEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeStatistics_Error() {
        when(animeService.getAnimeStatistics(TEST_ID)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getAnimeStatistics(TEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime statistics"));
    }

    @Test
    void getRecommendedAnime_Success() {
        when(animeService.getRecommendedAnime(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        ResponseEntity<String> response = animeController.getRecommendedAnime(TEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getRecommendedAnime_Error() {
        when(animeService.getRecommendedAnime(TEST_ID)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = animeController.getRecommendedAnime(TEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving recommended anime"));
    }

    @Test
    void getRecommendedAnime_NullResponse() {
        // Arrange
        when(animeService.getRecommendedAnime(TEST_ID)).thenReturn(null);

        // Act
        ResponseEntity<String> response = animeController.getRecommendedAnime(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }
}