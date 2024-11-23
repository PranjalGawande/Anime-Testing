package com.review.anime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.review.anime.Controller.UserController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class AnimeService {

    private static final Logger logger = LogManager.getLogger(AnimeService.class);

    private static final String JIKAN_API_URL = "https://api.jikan.moe/v4/";


    public List<String> getBackgroundImages() {
        logger.info("Fetching background images");
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(JIKAN_API_URL + "seasons/now", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> imageUrls = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode animeNode = rootNode.path("data"); // Access the "data" field
            Random random = new Random();
            int totalAnimeCount = animeNode.size();
            int count = Math.min(totalAnimeCount, 20); // Ensure we don't select more than total anime count
            for (int i = 0; i < count; i++) {
                int randomIndex = random.nextInt(totalAnimeCount);
                JsonNode anime = animeNode.get(randomIndex);
                JsonNode imageNode = anime.path("trailer").path("images");
                String imageUrl = imageNode.path("maximum_image_url").asText();
                imageUrls.add(imageUrl);
            }
            logger.info("Successfully fetched background images");
        } catch (Exception e) {
            logger.error("Error fetching background images", e);
            e.printStackTrace();
        }
        return imageUrls;
    }


    public String getCurrentSeasonAnime(String filter, Integer limit, Integer page) {
        RestTemplate restTemplate = new RestTemplate();
        logger.info("Fetching current season anime with filter: {}, limit: {}, page: {}", filter, limit, page);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "seasons/now?sfw=true");

        if (Objects.nonNull(filter)) {
            builder.queryParam("filter", filter);
        }

        if (Objects.nonNull(limit)) {
            builder.queryParam("limit", limit);
        }

        if (Objects.nonNull(page)) {
            builder.queryParam("page", page);
        }

        try {
            logger.info("Successfully fetched current season anime");
            return restTemplate.getForObject(builder.toUriString(), String.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching current season anime: {} - {}", e.getStatusCode(), e.getStatusText(), e);
            throw new RuntimeException("Error fetching current season anime: " + e.getStatusCode() + " - " + e.getStatusText());
        } catch (Exception e) {
            logger.error("Error fetching current season anime", e);
            throw new RuntimeException("Error fetching current season anime: " + e.getMessage());
        }
    }


    public String getUpcomingAnime(Integer page) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "seasons/upcoming?sfw=true");

            if (Objects.nonNull(page)) {
                builder.queryParam("page", page);
            }

            String response = restTemplate.getForObject(builder.toUriString(), String.class);
            logger.info("Successfully fetched upcoming anime");
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to upcoming anime API is forbidden", e);
            throw new RuntimeException("Access to upcoming anime API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving upcoming anime", e);
            throw new RuntimeException("Error retrieving upcoming anime: " + e.getMessage());
        }
    }


    public String getTopAnime(Integer page) {
        logger.info("Fetching top anime for page: {}", page);
        try {
            RestTemplate restTemplate = new RestTemplate();
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "top/anime?sfw=true");

            if (Objects.nonNull(page)) {
                builder.queryParam("page", page);
            }

            String response = restTemplate.getForObject(builder.toUriString(), String.class);
            logger.info("Successfully fetched top anime");
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to top anime API is forbidden", e);
            throw new RuntimeException("Access to top anime API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving top anime", e);
            throw new RuntimeException("Error retrieving top anime: " + e.getMessage());
        }
    }


    public String getTopCharacters() {
        logger.info("Fetching top characters");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(JIKAN_API_URL + "top/characters", String.class);
            logger.info("Successfully fetched top characters");
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to top characters API is forbidden", e);
            throw new RuntimeException("Access to top characters API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving top characters", e);
            throw new RuntimeException("Error retrieving top characters: " + e.getMessage());
        }
    }


    public String getRandomAnime() {
        logger.info("Fetching random anime");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(JIKAN_API_URL + "random/anime", String.class);
            logger.info("Successfully fetched random anime");
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to random anime API is forbidden", e);
            throw new RuntimeException("Access to random anime API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving random anime", e);
            throw new RuntimeException("Error retrieving random anime: " + e.getMessage());
        }
    }

    public String getAnimeGenres() {
        logger.info("Fetching anime genres");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(JIKAN_API_URL + "genres/anime?filter=genres", String.class);
            logger.info("Successfully fetched anime genres");
            return(response);
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to anime genres API is forbidden", e);
            throw new RuntimeException("Access to anime genres API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving anime genres", e);
            throw new RuntimeException("Error retrieving anime genres: " + e.getMessage());
        }
    }


    public String getAnimeExplore(Integer page, String genres) {
        logger.info("Exploring anime list with parameters: page={}, genres={}", page, genres);
        try {
            StringBuilder urlBuilder = new StringBuilder(JIKAN_API_URL + "anime?sfw=true");

            if (page != null) {
                urlBuilder.append("&page=").append(page);
            }
            if (genres != null && !genres.isEmpty()) {
                urlBuilder.append("&genres=").append(genres);
            }

            String url = urlBuilder.toString();

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            logger.info("Successfully explored anime list");
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to anime list API is forbidden", e);
            throw new RuntimeException("Access to anime list API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving anime list", e);
            throw new RuntimeException("Error retrieving anime list: " + e.getMessage());
        }
    }

    public String getAnimeSearch(String query, int page) {
        logger.info("Searching anime with query: {}, page: {}", query, page);
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = buildSearchUrl(query, page);
            String response = restTemplate.getForObject(url, String.class);
            logger.info("Successfully searched anime");
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to search anime API is forbidden", e);
            throw new RuntimeException("Error in accessing Anime Api.");
        } catch (Exception e) {
            logger.error("Error searching anime", e);
            throw new RuntimeException("Error searching anime: " + e.getMessage());
        }
    }

    private String buildSearchUrl(String query, int page) {
        return JIKAN_API_URL + "anime?q=" + query +
                "&page=" + page +
                "&sfw=true";
    }

    public String getAnimeDetailsById(String id) {
        logger.info("Fetching anime details for id: {}", id);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(JIKAN_API_URL + "anime/" + id + "/full", String.class);
            logger.info("Successfully fetched anime details for id: {}", id);
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("Anime details not found for ID: {}", id, e);
            return "Anime details not found for ID: " + id;
        } catch (Exception e) {
            logger.error("Error retrieving anime details for ID: {}", id, e);
            return "Error retrieving anime details for ID: " + id + ". " + e.getMessage();
        }
    }

    public String getAnimeCharactersList(String id) {
        logger.info("Fetching anime characters for id: {}", id);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(JIKAN_API_URL + "anime/" + id + "/characters", String.class);
            logger.info("Successfully fetched anime characters for id: {}", id);
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("Anime characters not found for ID: {}", id, e);
            return "Anime characters not found for ID: " + id;
        } catch (Exception e) {
            logger.error("Error retrieving anime characters for ID: {}", id, e);
            return "Error retrieving anime characters for ID: " + id + ". " + e.getMessage();
        }
    }

    public String getAnimeStaffList (String id) {
        logger.info("Fetching anime staff for id: {}", id);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(JIKAN_API_URL + "anime/" + id + "/staff", String.class);
            logger.info("Successfully fetched anime staff for id: {}", id);
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("Anime staff not found for ID: {}", id, e);
            return "Anime staff not found for ID: " + id;
        } catch (Exception e) {
            logger.error("Error retrieving anime staff for ID: {}", id, e);
            return "Error retrieving anime staff for ID: " + id + ". " + e.getMessage();
        }
    }


    public String getAnimeStatistics(String id) {
        logger.info("Fetching anime statistics for id: {}", id);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(JIKAN_API_URL + "anime/" + id + "/statistics", String.class);
            logger.info("Successfully fetched anime statistics for id: {}", id);
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("Anime statistics not found for ID: {}", id, e);
            return "Anime statistics not found for ID: " + id;
        } catch (Exception e) {
            logger.error("Error retrieving anime statistics for ID: {}", id, e);
            return "Error retrieving anime statistics for ID: " + id + ". " + e.getMessage();
        }
    }

    public String getRecommendedAnime(String id) {
        logger.info("Fetching recommended anime for id: {}", id);
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(JIKAN_API_URL + "anime/" + id + "/recommendations", String.class);
            logger.info("Successfully fetched recommended anime for id: {}", id);
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Access to recommended anime API is forbidden", e);
            throw new RuntimeException("Access to recommended anime API is forbidden. Check authorization.");
        } catch (Exception e) {
            logger.error("Error retrieving recommended anime for id: {}", id, e);
            throw new RuntimeException("Error retrieving recommended anime: " + e.getMessage());
        }
    }

}
