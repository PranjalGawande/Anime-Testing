package com.review.anime.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.review.anime.service.AnimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Random random;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBackgroundImages_Success() throws Exception {
        String mockResponse = "{\"data\":[{\"trailer\":{\"images\":{\"maximum_image_url\":\"url1\"}}},{\"trailer\":{\"images\":{\"maximum_image_url\":\"url2\"}}}]}";
        JsonNode mockRootNode = new ObjectMapper().readTree(mockResponse);

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        when(objectMapper.readTree(mockResponse))
                .thenReturn(mockRootNode);

        List<String> result = animeService.getBackgroundImages();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("url1"));
        assertTrue(result.contains("url2"));

        verify(restTemplate).getForObject(anyString(), eq(String.class));
        verify(objectMapper).readTree(anyString());
    }

    @Test
    void testGetBackgroundImages_Error() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> animeService.getBackgroundImages());
        assertTrue(exception.getMessage().contains("Error fetching background images:"));

        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetBackgroundImages_NullResponse() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(null);

        List<String> result = animeService.getBackgroundImages();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty list due to null response");

        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetBackgroundImages_EmptyResponseString() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("");

        List<String> result = animeService.getBackgroundImages();
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty list due to empty string response");

        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetCurrentSeasonAnime_Success() throws Exception {
        String mockResponse = "{\"data\":[{\"title\":\"Anime1\"},{\"title\":\"Anime2\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String response = animeService.getCurrentSeasonAnime(null, null, null);

        assertNotNull(response);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println(response);

        List<String> titles = new ArrayList<>();
        for (JsonNode node : jsonNode.get("data")) {
            titles.add(node.get("title").asText());
        }

        assertEquals(2, titles.size());
        assertTrue(titles.contains("Anime1"));
        assertTrue(titles.contains("Anime2"));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetCurrentSeasonAnime_Error() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> animeService.getCurrentSeasonAnime(null, null, null));
        assertTrue(exception.getMessage().contains("Error fetching current season anime:"));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetCurrentSeasonAnime_EmptyResponse() throws Exception {
        String mockResponse = "{\"data\":[]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String response = animeService.getCurrentSeasonAnime(null, null, null);

        assertNotNull(response, "Response should not be null");
        assertEquals(mockResponse, response, "The response should match the mocked empty JSON");

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getCurrentSeasonAnime_InvalidResponse() {
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(null);

        String result = animeService.getCurrentSeasonAnime("tv", 1, 1);

        assertEquals("No data available for current season anime", result);
    }

    @Test
    void testGetUpcomingAnime_Success() throws Exception {
        String mockResponse = "{\"data\":[{\"title\":\"UpcomingAnime1\"},{\"title\":\"UpcomingAnime2\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String response = animeService.getUpcomingAnime(1);

        assertNotNull(response);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println(response);

        List<String> titles = new ArrayList<>();
        for (JsonNode node : jsonNode.get("data")) {
            titles.add(node.get("title").asText());
        }

        assertEquals(2, titles.size());
        assertTrue(titles.contains("UpcomingAnime1"));
        assertTrue(titles.contains("UpcomingAnime2"));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetUpcomingAnime_Error() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> animeService.getUpcomingAnime(1));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getStatusText().contains("Bad Request"));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }


    @Test
    void testGetTopAnime_Success() throws Exception {
        String mockResponse = "{\"data\":[{\"title\":\"TopAnime1\"},{\"title\":\"TopAnime2\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String result = animeService.getTopAnime(1);

        assertNotNull(result);
        assertTrue(result.contains("TopAnime1"));
        assertTrue(result.contains("TopAnime2"));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetTopAnime_EmptyResponse() {
        String mockResponse = "";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getTopAnime(1);

        assertEquals("No data available for top anime", result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetTopAnime_ValidResponse() {
        String mockResponse = "{\"data\": [{\"name\": \"Attack on Titan\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getTopAnime(1);

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetTopAnime_InvalidResponse() {
        String mockResponse = "{}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String result = animeService.getTopAnime(1);

        assertNotNull(result);
        assertEquals("{}", result);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getTopAnime_WithInvalidPage() {
        String expectedResponse = "{\"data\": []}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getTopAnime(-1);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getBackgroundImages_LargeDataSet() throws Exception {
        StringBuilder jsonBuilder = new StringBuilder("{\"data\": [");
        for (int i = 0; i < 30; i++) {
            if (i > 0) jsonBuilder.append(",");
            jsonBuilder.append("{\"trailer\": {\"images\": {\"maximum_image_url\": \"url").append(i).append("\"}}}");
        }
        jsonBuilder.append("]}");

        String jsonResponse = jsonBuilder.toString();

        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(jsonResponse);
        when(objectMapper.readTree(any(String.class)))
                .thenReturn(new ObjectMapper().readTree(jsonResponse));

        List<String> results = animeService.getBackgroundImages();

        assertAll(
                () -> assertTrue(results.size() <= 20, "Should not exceed MAX_IMAGES"),
                () -> assertTrue(results.stream().distinct().count() == results.size(), "Should have no duplicates"),
                () -> assertTrue(results.stream().allMatch(url -> url.startsWith("url")))
        );
    }

    @Test
    void getAnimeGenres_Success() {
        String expectedResponse = "{\"data\": [{\"id\": 1, \"name\": \"Action\"}, {\"id\": 2, \"name\": \"Comedy\"}]}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeGenres();

        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetAnimeGenres_EmptyResponse() {
        String mockResponse = "";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeGenres();

        assertEquals("No data available for anime genres", result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetAnimeGenres_ValidResponse() {
        String mockResponse = "{\"data\": [{\"genre\": \"Action\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeGenres();

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }


    @Test
    void getAnimeStatistics_Success() {
        String expectedResponse = "{\"data\": {\"watching\": 1000, \"completed\": 500}}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeStatistics("1");

        assertEquals(expectedResponse, result);
    }

    @Test
    void getRandomAnime_Success() {
        String expectedResponse = "{\"data\": {\"title\": \"Test Anime\"}}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getRandomAnime();

        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetRandomAnime_EmptyResponse() {
        String mockResponse = "";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getRandomAnime();

        assertEquals("No data available for random anime", result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetRandomAnime_ValidResponse() {
        String mockResponse = "{\"data\": [{\"title\": \"One Piece\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getRandomAnime();

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }


    @Test
    void getAnimeCharactersList_Success() {
        String expectedResponse = "{\"data\": [{\"character\": {\"name\": \"Test Character\"}}]}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeCharactersList("1");

        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetTopCharacters_EmptyResponse() {
        String mockResponse = "";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getTopCharacters();

        assertEquals("No data available for top characters", result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetTopCharacters_ValidResponse() {
        String mockResponse = "{\"data\": [{\"name\": \"Naruto Uzumaki\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getTopCharacters();

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }


    @Test
    void getAnimeStaffList_Success() {
        String expectedResponse = "{\"data\": [{\"person\": {\"name\": \"Test Staff\"}}]}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeStaffList("1");

        assertEquals(expectedResponse, result);
    }

    @Test
    void getRecommendedAnime_Success() {
        String expectedResponse = "{\"data\": [{\"entry\": {\"title\": \"Recommended Anime\"}}]}";
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getRecommendedAnime("1");

        assertEquals(expectedResponse, result);
    }

    @Test
    void getAnimeExplore_WithInvalidGenres() {
        String expectedResponse = "{\"data\": []}";

        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeExplore(1, "invalid");

        assertEquals(expectedResponse, result);
    }

    @Test
    void getAnimeExplore_WithNullGenres() {
        String expectedResponse = "{\"data\": []}";

        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeExplore(1, null);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getAnimeExplore_WithValidGenres() {
        String expectedResponse = "{\"data\": [{\"title\": \"Anime Title\", \"genre\": \"Action\"}]}";

        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = animeService.getAnimeExplore(1, "Action");

        assertEquals(expectedResponse, result);
    }


    @Test
    void testGetAnimeSearch_EmptyQuery() {
        String query = "";
        int page = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            animeService.getAnimeSearch(query, page);
        });

        assertEquals("Search query cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetAnimeSearch_ValidResponse() {
        String query = "Naruto";
        int page = 1;
        String mockResponse = "{\"data\": [{\"name\": \"Naruto\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeSearch(query, page);

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetAnimeDetailsById_EmptyResponse() {
        String mockResponse = "";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeDetailsById("1");

        assertEquals("No data available for anime details", result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetAnimeDetailsById_ValidResponse() {
        String mockResponse = "{\"data\": [{\"name\": \"Naruto\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeDetailsById("1");

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetAnimeCharactersList_EmptyResponse() {
        String mockResponse = "";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeCharactersList("1");

        assertEquals("No data available for anime characters", result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetAnimeCharactersList_ValidResponse() {
        String mockResponse = "{\"data\": [{\"name\": \"Naruto Uzumaki\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String result = animeService.getAnimeCharactersList("1");

        assertNotNull(result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }
}
