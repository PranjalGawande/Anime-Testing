package com.review.anime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class AnimeService {

    private static final Logger logger = LogManager.getLogger(AnimeService.class);
    private static final String JIKAN_API_URL = "https://api.jikan.moe/v4/";
    private static final int MAX_IMAGES = 20;
//    private final RestTemplate restTemplate = new RestTemplate();
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Random random;  // Add Random as a dependency

    @Autowired
    public AnimeService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.random = new Random();  // Or inject this too if needed
    }

    public List<String> getBackgroundImages() {
        logger.info("Fetching background images");
        List<String> imageUrls = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(JIKAN_API_URL + "seasons/now", String.class);
            logger.info("API Response received");

            if (response == null || response.isEmpty()) {
                logger.warn("Empty response received from background images API");
                return imageUrls;
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode animeNode = rootNode.path("data");

            if (!animeNode.isArray() || animeNode.isEmpty()) {
                logger.warn("No anime data found in background images API");
                return imageUrls;
            }

            int totalAnimeCount = animeNode.size();
            Set<Integer> usedIndices = new HashSet<>();
            int count = Math.min(totalAnimeCount, MAX_IMAGES);

            for (int i = 0; i < count && usedIndices.size() < totalAnimeCount; i++) {
                int randomIndex;
                do {
                    randomIndex = random.nextInt(totalAnimeCount);
                } while (usedIndices.contains(randomIndex) && usedIndices.size() < totalAnimeCount);

                usedIndices.add(randomIndex);

                JsonNode anime = animeNode.get(randomIndex);
                String imageUrl = anime.path("trailer")
                        .path("images")
                        .path("maximum_image_url")
                        .asText(null);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrls.add(imageUrl);
                }
            }

            logger.info("Successfully fetched {} background images", imageUrls.size());

        } catch (HttpClientErrorException e) {
            logger.error("HTTP error while fetching background images: {} - {}", e.getStatusCode(), e.getStatusText(), e);
            throw new RuntimeException("Error fetching background images: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching background images", e);
            throw new RuntimeException("Unexpected error while fetching background images", e);
        }

        return imageUrls;
    }

    public String getCurrentSeasonAnime(String filter, Integer limit, Integer page) {
        logger.info("Fetching current season anime with filter: {}, limit: {}, page: {}", filter, limit, page);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "seasons/now?sfw=true")
                .queryParamIfPresent("filter", Objects.nonNull(filter) ? java.util.Optional.of(filter) : java.util.Optional.empty())
                .queryParamIfPresent("limit", Objects.nonNull(limit) ? java.util.Optional.of(limit) : java.util.Optional.empty())
                .queryParamIfPresent("page", Objects.nonNull(page) ? java.util.Optional.of(page) : java.util.Optional.empty());

        try {
            return executeApiCall(builder.toUriString(), "current season anime");
        } catch (HttpClientErrorException e) {
            // Catch HttpClientErrorException thrown from executeApiCall and throw RuntimeException
            throw new RuntimeException("Error fetching current season anime: " + e.getStatusCode() + " - " + e.getStatusText(), e);
        }
    }



    public String getUpcomingAnime(Integer page) {
        logger.info("Fetching upcoming anime for page: {}", page);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "seasons/upcoming?sfw=true")
                .queryParamIfPresent("page", Objects.nonNull(page) ? java.util.Optional.of(page) : java.util.Optional.empty());

        return executeApiCall(builder.toUriString(), "upcoming anime");
    }

    public String getTopAnime(Integer page) {
        logger.info("Fetching top anime for page: {}", page);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "top/anime?sfw=true")
                .queryParamIfPresent("page", Objects.nonNull(page) ? java.util.Optional.of(page) : java.util.Optional.empty());

        return executeApiCall(builder.toUriString(), "top anime");
    }

    public String getTopCharacters() {
        logger.info("Fetching top characters");
        return executeApiCall(JIKAN_API_URL + "top/characters", "top characters");
    }

    public String getRandomAnime() {
        logger.info("Fetching random anime");
        return executeApiCall(JIKAN_API_URL + "random/anime", "random anime");
    }

    public String getAnimeGenres() {
        logger.info("Fetching anime genres");
        return executeApiCall(JIKAN_API_URL + "genres/anime?filter=genres", "anime genres");
    }

    public String getAnimeExplore(Integer page, String genres) {
        logger.info("Exploring anime list with page={} and genres={}", page, genres);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JIKAN_API_URL + "anime?sfw=true")
                .queryParamIfPresent("page", Objects.nonNull(page) ? java.util.Optional.of(page) : java.util.Optional.empty())
                .queryParamIfPresent("genres", Objects.nonNull(genres) && !genres.isEmpty() ? java.util.Optional.of(genres) : java.util.Optional.empty());

        return executeApiCall(builder.toUriString(), "anime exploration");
    }

    public String getAnimeSearch(String query, int page) {
        logger.info("Searching anime with query: {} and page: {}", query, page);

        if (query == null || query.isEmpty()) {
            logger.warn("Search query cannot be null or empty");
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }

        String url = JIKAN_API_URL + "anime?q=" + query + "&page=" + page + "&sfw=true";
        return executeApiCall(url, "anime search");
    }

    public String getAnimeDetailsById(String id) {
        logger.info("Fetching anime details for id: {}", id);
        return executeApiCall(JIKAN_API_URL + "anime/" + id + "/full", "anime details");
    }

    public String getAnimeCharactersList(String id) {
        logger.info("Fetching anime characters for id: {}", id);
        return executeApiCall(JIKAN_API_URL + "anime/" + id + "/characters", "anime characters");
    }

    public String getAnimeStaffList(String id) {
        logger.info("Fetching anime staff for id: {}", id);
        return executeApiCall(JIKAN_API_URL + "anime/" + id + "/staff", "anime staff");
    }

    public String getAnimeStatistics(String id) {
        logger.info("Fetching anime statistics for id: {}", id);
        return executeApiCall(JIKAN_API_URL + "anime/" + id + "/statistics", "anime statistics");
    }

    public String getRecommendedAnime(String id) {
        logger.info("Fetching recommended anime for id: {}", id);
        return executeApiCall(JIKAN_API_URL + "anime/" + id + "/recommendations", "recommended anime");
    }

    private String executeApiCall(String url, String context) {
        try {
            logger.info("Executing API call for {}", context);
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isEmpty()) {
                logger.warn("Empty response received for {}", context);
                return "No data available for " + context;
            }
            logger.info("Successfully fetched {}", context);
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error during API call for {}: {} - {}", context, e.getStatusCode(), e.getStatusText(), e);
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        } catch (Exception e) {
            logger.error("Unexpected error during API call for {}", context, e);
            throw new RuntimeException("Error fetching " + context + ": " + e.getMessage());
        }
    }
}
