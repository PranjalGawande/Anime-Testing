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
    void testGetCurrentSeasonAnime_Success() throws Exception {
        String mockResponse = "{\"data\":[{\"title\":\"Anime1\"},{\"title\":\"Anime2\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String response = animeService.getCurrentSeasonAnime(null, null, null);

        // Assert response is not null
        assertNotNull(response);

        // Parse the response using ObjectMapper (or another JSON library)
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println(response);

        // Extract titles from the JSON response
        List<String> titles = new ArrayList<>();
        for (JsonNode node : jsonNode.get("data")) {
            titles.add(node.get("title").asText());
        }

        // Verify titles
        assertEquals(2, titles.size());
        assertTrue(titles.contains("Anime1"));
        assertTrue(titles.contains("Anime2"));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }


    @Test
    void testGetCurrentSeasonAnime_EmptyResponse() throws Exception {
        // Mock the empty response from the API
        String mockResponse = "{\"data\":[]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        // Call the service method with null parameters (or specific values if needed)
        String response = animeService.getCurrentSeasonAnime(null, null, null);

        // Assertions
        assertNotNull(response, "Response should not be null");
        assertEquals(mockResponse, response, "The response should match the mocked empty JSON");

        // Verify that the REST call was made exactly once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }


    @Test
    void testGetCurrentSeasonAnime_HttpClientErrorException() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> animeService.getCurrentSeasonAnime(null, null, null));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Error fetching current season anime: 400 BAD_REQUEST - Bad Request"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }




    @Test
    void testGetBackgroundImages_Success() throws Exception {
        // Prepare test data
        String mockResponse = "{\"data\":[{\"trailer\":{\"images\":{\"maximum_image_url\":\"url1\"}}},{\"trailer\":{\"images\":{\"maximum_image_url\":\"url2\"}}}]}";
        JsonNode mockRootNode = new ObjectMapper().readTree(mockResponse);

        // Mock the restTemplate.getForObject call
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        // Mock the objectMapper.readTree call
        when(objectMapper.readTree(mockResponse))
                .thenReturn(mockRootNode);

        // Execute
        List<String> result = animeService.getBackgroundImages();
//        System.out.println(result);
        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("url1"));
        assertTrue(result.contains("url2"));

        // Verify interactions
        verify(restTemplate).getForObject(anyString(), eq(String.class));
        verify(objectMapper).readTree(anyString());
    }

    @Test
    void testGetBackgroundImages_NullResponse() throws Exception {
        // Mocking null response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(null);

        List<String> result = animeService.getBackgroundImages();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty list due to null response");

        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetBackgroundImages_EmptyResponseString() throws Exception {
        // Mocking empty string response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("");

        List<String> result = animeService.getBackgroundImages();
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty list due to empty string response");

        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }


//    @Test
//    void testGetBackgroundImages_EmptyResponse() throws Exception {
//        // Prepare empty response
//        String mockResponse = "{\"data\":[]}";
//        JsonNode mockRootNode = new ObjectMapper().readTree(mockResponse);
//
//        // Mock getForObject instead of getForEntity
//        when(restTemplate.getForObject(anyString(), eq(String.class)))
//                .thenReturn(mockResponse);
//
//        when(objectMapper.readTree(mockResponse))
//                .thenReturn(mockRootNode);
//
//        List<String> result = animeService.getBackgroundImages();
//
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//
//        verify(restTemplate).getForObject(anyString(), eq(String.class));
//        verify(objectMapper).readTree(anyString());
//    }

//    @Test
//    void testGetBackgroundImages_EmptyResponse() {
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(""); // Mocking empty response
//        List<String> images = animeService.getBackgroundImages();
//        assertTrue(images.isEmpty(), "Expected empty list due to empty response");
//    }

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
    void testGetTopAnime_Success() throws Exception {
        // Mock the API response
        String mockResponse = "{\"data\":[{\"title\":\"TopAnime1\"},{\"title\":\"TopAnime2\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        // Call the service method with a valid page number
        String result = animeService.getTopAnime(1);

        // Assert the response content
        assertNotNull(result);
        assertTrue(result.contains("TopAnime1"));
        assertTrue(result.contains("TopAnime2"));

        // Verify RestTemplate was called once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }


    @Test
    void testGetTopAnime_InvalidResponse() {
        // Mock an invalid API response
        String mockResponse = "{}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        // Call the service method with a valid page number
        String result = animeService.getTopAnime(1);

        // Assert the response is not null and contains invalid structure
        assertNotNull(result);
        assertEquals("{}", result); // Validate the raw response for invalid structure

        // Verify RestTemplate was called once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getBackgroundImages_LargeDataSet() throws Exception {
        // Create a large dataset
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
    void getCurrentSeasonAnime_InvalidResponse() {
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(null);

        String result = animeService.getCurrentSeasonAnime("tv", 1, 1);

        assertEquals("No data available for current season anime", result);
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
    void getAnimeExplore_WithInvalidGenres() {
        // Expected response when providing an invalid genre
        String expectedResponse = "{\"data\": []}";

        // Mocking the API response for an invalid genre
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        // Call the method with an invalid genre (e.g., "invalid" genre)
        String result = animeService.getAnimeExplore(1, "invalid");

        // Validate that the correct response is returned
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAnimeExplore_WithNullGenres() {
        // Expected response when genre is null
        String expectedResponse = "{\"data\": []}";

        // Mocking the API response for a null genre
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        // Call the method with null genres
        String result = animeService.getAnimeExplore(1, null);

        // Validate that the correct response is returned
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAnimeExplore_WithValidGenres() {
        // Expected response when providing valid genres
        String expectedResponse = "{\"data\": [{\"title\": \"Anime Title\", \"genre\": \"Action\"}]}";

        // Mocking the API response for a valid genre
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn(expectedResponse);

        // Call the method with a valid genre (e.g., "Action")
        String result = animeService.getAnimeExplore(1, "Action");

        // Validate that the correct response is returned
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
