//package com.review.anime.Service;
//
//import com.review.anime.service.AnimeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AnimeServiceTest {
//
//    @InjectMocks
//    private AnimeService animeService;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    private static final String MOCK_URL = "https://api.jikan.moe/v4/";
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetBackgroundImages() throws InterruptedException {
//        String mockResponse = """
//        {
//            "data": [
//                {
//                    "trailer": {
//                        "images": {
//                            "maximum_image_url": "https://example.com/image1.jpg"
//                        }
//                    }
//                },
//                {
//                    "trailer": {
//                        "images": {
//                            "maximum_image_url": "https://example.com/image2.jpg"
//                        }
//                    }
//                }
//            ]
//        }""";
//
//        when(restTemplate.getForObject(MOCK_URL + "seasons/now", String.class)).thenReturn(mockResponse);
//
//        var images = animeService.getBackgroundImages();
//        assertNotNull(images);
//        assertEquals(2, images.size());
//        assertEquals("https://example.com/image1.jpg", images.get(0));
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetCurrentSeasonAnime() throws InterruptedException {
//        // Mock response matching the API's structure
//        String mockResponse = """
//        {
//          "data": [{}],
//          "pagination": {
//            "last_visible_page": 0,
//            "has_next_page": true,
//            "items": {}
//          }
//        }
//    """;
//
//        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
//                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
//
//        // Call the service method
//        String response = animeService.getCurrentSeasonAnime(null, null, null);
//
//        // Verify the response and behavior
//        assertNotNull(response);
//        assertTrue(response.contains("\"data\": [{}]"));
//        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class));
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetUpcomingAnime() throws InterruptedException {
//        String mockResponse = "{\"data\": []}";
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        var response = animeService.getUpcomingAnime(1);
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetTopAnime() throws InterruptedException {
//        String mockResponse = """
//        {
//          "data": [{}],
//          "pagination": {
//            "last_visible_page": 0,
//            "has_next_page": true,
//            "items": {}
//          }
//        }
//    """;
//
//        // Mocking the response of the API call
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        // Call the service method
//        var response = animeService.getTopAnime(1);
//
//        // Asserting the response
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate a longer delay to avoid rate limiting issues
//        Thread.sleep(1000); // Wait for 1 second to ensure we don't exceed the rate limit
//    }
//
//
//    @Test
//    void testGetTopCharacters() throws InterruptedException {
//        String mockResponse = "{\"data\": []}";
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        var response = animeService.getTopCharacters();
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetRandomAnime() throws InterruptedException {
//        String mockResponse = "{\"data\": {}}";
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        var response = animeService.getRandomAnime();
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetAnimeGenres() throws InterruptedException {
//        // Simulate API request
//        String mockResponse = """
//        {
//          "data": [
//            {"mal_id": 4, "type": "anime", "name": "Comedy", "url": "https://myanimelist.net/anime/genre/4/Comedy"},
//            {"mal_id": 10, "type": "anime", "name": "Fantasy", "url": "https://myanimelist.net/anime/genre/10/Fantasy"},
//            {"mal_id": 47, "type": "anime", "name": "Gourmet", "url": "https://myanimelist.net/anime/genre/47/Gourmet"}
//          ]
//        }
//    """;
//
//        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
//                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
//
//        String response = animeService.getAnimeGenres();
//
//        assertNotNull(response);
//        assertTrue(response.contains("\"name\":\"Comedy\""));
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetAnimeDetailsById() throws InterruptedException {
//        String mockResponse = "{\"data\": {}}";
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        var response = animeService.getAnimeDetailsById("12345");
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetAnimeCharactersList() throws InterruptedException {
//        String mockResponse = "{\"data\": []}";
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        var response = animeService.getAnimeCharactersList("12345");
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//
//    @Test
//    void testGetAnimeStaffList() throws InterruptedException {
//        String mockResponse = "{\"data\": []}";
//        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
//
//        var response = animeService.getAnimeStaffList("12345");
//        assertNotNull(response);
//        assertEquals(mockResponse, response);
//
//        // Simulate delay for rate limiting
//        Thread.sleep(350); // Wait to adhere to 3 requests/second
//    }
//}
