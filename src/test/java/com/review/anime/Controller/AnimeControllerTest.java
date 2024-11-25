package com.review.anime.Controller;

import com.review.anime.service.AnimeService;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        // Common setup code if needed
    }

    @Test
    void getBackgroundImages_Success() {
        // Arrange
        List<String> expectedImages = Arrays.asList("image1.jpg", "image2.jpg");
        when(animeService.getBackgroundImages()).thenReturn(expectedImages);

        // Act
        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedImages, response.getBody());
    }

    @Test
    void getBackgroundImages_Error() {
        // Arrange
        when(animeService.getBackgroundImages()).thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentSeasonAnime_Success() {
        // Arrange
        when(animeService.getCurrentSeasonAnime(anyString(), anyInt(), anyInt()))
                .thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getCurrentSeasonAnime("popular", 10, 1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getUpcomingAnime_Success() {
        // Arrange
        when(animeService.getUpcomingAnime(anyInt())).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getUpcomingAnime(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getTopAnime_Success() {
        // Arrange
        when(animeService.getTopAnime(anyInt())).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getTopAnime(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getTopCharacters_Success() {
        // Arrange
        when(animeService.getTopCharacters()).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getTopCharacters();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getRandomAnime_Success() {
        // Arrange
        when(animeService.getRandomAnime()).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getRandomAnime();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeGenres_Success() {
        // Arrange
        when(animeService.getAnimeGenres()).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getAnimeGenres();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void exploreAnimeList_Success() {
        // Arrange
        when(animeService.getAnimeExplore(anyInt(), anyString())).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.exploreAnimeList(1, "action,comedy");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void searchAnime_Success() {
        // Arrange
        when(animeService.getAnimeSearch(anyString(), anyInt())).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.searchAnime("Naruto", 1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeDetailsById_Success() {
        // Arrange
        when(animeService.getAnimeDetailsById(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getAnimeDetailsById(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeCharactersList_Success() {
        // Arrange
        when(animeService.getAnimeCharactersList(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getAnimeCharactersList(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeStaffList_Success() {
        // Arrange
        when(animeService.getAnimeStaffList(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getAnimeStaffList(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getAnimeStatistics_Success() {
        // Arrange
        when(animeService.getAnimeStatistics(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getAnimeStatistics(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    @Test
    void getRecommendedAnime_Success() {
        // Arrange
        when(animeService.getRecommendedAnime(TEST_ID)).thenReturn(MOCK_ANIME_DATA);

        // Act
        ResponseEntity<String> response = animeController.getRecommendedAnime(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ANIME_DATA, response.getBody());
    }

    // Error test cases for each endpoint
    @Test
    void getCurrentSeasonAnime_Error() {
        // Arrange
        when(animeService.getCurrentSeasonAnime(anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<String> response = animeController.getCurrentSeasonAnime("popular", 10, 1);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving current season anime"));
    }

    @Test
    void searchAnime_Error() {
        // Arrange
        when(animeService.getAnimeSearch(anyString(), anyInt()))
                .thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<String> response = animeController.searchAnime("Naruto", 1);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error searching anime"));
    }

    @Test
    void getAnimeDetailsById_Error() {
        // Arrange
        when(animeService.getAnimeDetailsById(TEST_ID))
                .thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<String> response = animeController.getAnimeDetailsById(TEST_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error retrieving anime details"));
    }







    @Test
    void getBackgroundImages_EmptyList() {
        // Arrange
        when(animeService.getBackgroundImages()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getBackgroundImages_NullList() {
        // Arrange
        when(animeService.getBackgroundImages()).thenReturn(null);

        // Act
        ResponseEntity<List<String>> response = animeController.getBackgroundImages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
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
    void searchAnime_EmptyQuery() {
        // Arrange
        String emptyQuery = "";
        when(animeService.getAnimeSearch(emptyQuery, 1)).thenThrow(new IllegalArgumentException("Query cannot be empty"));

        // Act
        ResponseEntity<String> response = animeController.searchAnime(emptyQuery, 1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Query cannot be empty"));
    }

    @Test
    void exploreAnimeList_InvalidGenre() {
        // Arrange
        String invalidGenre = "unknown";
        when(animeService.getAnimeExplore(anyInt(), eq(invalidGenre)))
                .thenThrow(new IllegalArgumentException("Invalid genre"));

        // Act
        ResponseEntity<String> response = animeController.exploreAnimeList(1, invalidGenre);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid genre"));
    }

    @Test
    void getCurrentSeasonAnime_InvalidPageNumber() {
        // Arrange
        int invalidPage = -1;
        when(animeService.getCurrentSeasonAnime(anyString(), anyInt(), eq(invalidPage)))
                .thenThrow(new IllegalArgumentException("Invalid page number"));

        // Act
        ResponseEntity<String> response = animeController.getCurrentSeasonAnime("popular", 10, invalidPage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid page number"));
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
    void getAnimeCharactersList_EmptyResponse() {
        // Arrange
        when(animeService.getAnimeCharactersList(TEST_ID)).thenReturn("");

        // Act
        ResponseEntity<String> response = animeController.getAnimeCharactersList(TEST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void getTopAnime_NegativePageNumber() {
        // Arrange
        int negativePage = -2;
        when(animeService.getTopAnime(negativePage))
                .thenThrow(new IllegalArgumentException("Page number cannot be negative"));

        // Act
        ResponseEntity<String> response = animeController.getTopAnime(negativePage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Page number cannot be negative"));
    }

    @Test
    void getTopAnime_InvalidPageNumber() {
        // Arrange
        int invalidPage = -1;
        when(animeService.getTopAnime(eq(invalidPage)))
                .thenThrow(new IllegalArgumentException("Invalid page number"));

        // Act
        ResponseEntity<String> response = animeController.getTopAnime(invalidPage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid page number"));
    }


    @Test
    void getTopCharacters_EmptyResponse() {
        // Arrange
        when(animeService.getTopCharacters()).thenReturn("");

        // Act
        ResponseEntity<String> response = animeController.getTopCharacters();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void exploreAnimeList_NullResponse() {
        // Arrange: Define the scenario where getAnimeExplore might return null
        Integer page = 1;
        String genres = "action";

        // Simulate the behavior of the animeService to return null
        when(animeService.getAnimeExplore(page, genres)).thenReturn(null);

        // Act: Call the exploreAnimeList method
        ResponseEntity<String> response = animeController.exploreAnimeList(page, genres);

        // Assert: Verify that the response is 500 internal server error or any other custom handling for null
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error retrieving anime details: No data available", response.getBody());
    }

}