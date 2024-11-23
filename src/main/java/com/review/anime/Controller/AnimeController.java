package com.review.anime.Controller;

import com.review.anime.service.AnimeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/anime", produces = "application/json")
public class AnimeController {

    private static final Logger logger = LogManager.getLogger(AnimeController.class);

    @Autowired
    private AnimeService animeService;

    @GetMapping("/background-images")
    public ResponseEntity<List<String>> getBackgroundImages() {
        try {
            List<String> backgroundImages = animeService.getBackgroundImages();
            logger.info("Successfully fetched background images");
            return ResponseEntity.ok(backgroundImages);
        } catch (Exception e) {
            logger.error("Error fetching background images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/current-season")
    public ResponseEntity<String> getCurrentSeasonAnime(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "page", required = false) Integer page){
        try {
            String currentSeasonAnime = animeService.getCurrentSeasonAnime(filter, limit, page);
            logger.info("Successfully fetched current season anime");
            return ResponseEntity.ok(currentSeasonAnime);
        } catch (Exception e) {
            logger.error("Error retrieving current season anime", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving current season anime: " + e.getMessage());
        }
    }

    @GetMapping("/upcoming-anime")
    public ResponseEntity<String> getUpcomingAnime(
            @RequestParam(value = "page", required = false) Integer page
    ) {
        logger.info("Fetching upcoming anime for page: {}", page);
        try {
            String upcomingAnime = animeService.getUpcomingAnime(page);
            logger.info("Successfully fetched upcoming anime");
            return ResponseEntity.ok(upcomingAnime);
        } catch (Exception e) {
            logger.error("Error retrieving upcoming anime", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving upcoming anime: " + e.getMessage());
        }
    }


    @GetMapping("/top-anime")
    public ResponseEntity<String> getTopAnime(
            @RequestParam(value = "page", required = false) Integer page
    ) {
        logger.info("Fetching top anime for page: {}", page);
        try {
            String topAnime = animeService.getTopAnime(page);
            logger.info("Successfully fetched top anime");
            return ResponseEntity.ok(topAnime);
        } catch (Exception e) {
            logger.error("Error retrieving top anime", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving top anime: " + e.getMessage());
        }
    }

    @GetMapping("/top-characters")
    public ResponseEntity<String> getTopCharacters() {
        logger.info("Fetching top characters");
        try {
            String topCharacters = animeService.getTopCharacters();
            logger.info("Successfully fetched top characters");
            return ResponseEntity.ok(topCharacters);
        } catch (Exception e) {
            logger.error("Error retrieving top characters", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving top characters: " + e.getMessage());
        }
    }


    @GetMapping("/random-anime")
    public ResponseEntity<String> getRandomAnime() {
        logger.info("Fetching random anime");
        try {
            String randomAnime = animeService.getRandomAnime();
            logger.info("Successfully fetched random anime");
            return ResponseEntity.ok(randomAnime);
        }
        catch (Exception e) {
            logger.error("Error retrieving random anime", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving random anime: " + e.getMessage());
        }
    }

    @GetMapping("/get-anime-genres")
    public ResponseEntity<String> getAnimeGenres() {
        logger.info("Fetching anime genres");
        try {
            String animeGenres = animeService.getAnimeGenres();
            logger.info("Successfully fetched anime genres");
            return ResponseEntity.ok(animeGenres);
        } catch (Exception e) {
            logger.error("Error retrieving anime genres", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving anime genres: " + e.getMessage());
        }
    }

    @GetMapping("explore")
    public ResponseEntity<String> exploreAnimeList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String genres
    ) {
        logger.info("Fetching explore anime list with parameters: page={}, genres={}", page, genres);
        try {
            String animeDetails = animeService.getAnimeExplore(
                    page, genres
            );
            logger.info("Successfully fetched explore anime list");
            return ResponseEntity.ok(animeDetails);
        } catch (Exception e) {
            logger.error("Error retreving explore anime list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving anime details: " + e.getMessage());
        }
    }

    @GetMapping("search")
    public ResponseEntity<String> searchAnime(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        logger.info("Searching anime with query: {}, page: {}", query, page);
        try {
            String animeDetails = animeService.getAnimeSearch(query, page);
            logger.info("Successfully searched anime");
            return ResponseEntity.ok(animeDetails);
        } catch (Exception e) {
            logger.error("Error searching anime", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching anime: " + e.getMessage());
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<String> getAnimeDetailsById(@PathVariable("id") String id) {
        logger.info("Fetching anime details for id: {}", id);
        try {
            String animeDetails = animeService.getAnimeDetailsById(id);
            logger.info("Successfully fetched anime details for id: {}", id);
            return ResponseEntity.ok(animeDetails);
        } catch (Exception e) {
            logger.error("Error retrieving anime details for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving anime details: " + e.getMessage());
        }
    }

    @GetMapping("/characters-list/{id}")
    public ResponseEntity<String> getAnimeCharactersList(@PathVariable("id") String id) {
        logger.info("Fetching anime characters list for id: {}", id);
        try {
            String charactersList = animeService.getAnimeCharactersList(id);
            logger.info("Successfully fetched anime characters list for id: {}", id);
            return ResponseEntity.ok(charactersList);
        } catch (Exception e) {
            logger.error("Error retrieving anime characters list for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving anime characters list: " + e.getMessage());
        }
    }

    @GetMapping("/staff-list/{id}")
    public ResponseEntity<String> getAnimeStaffList(@PathVariable("id") String id) {
        logger.info("Fetching anime staff list for id: {}", id);
        try {
            String staffList = animeService.getAnimeStaffList(id);
            logger.info("Successfully fetched anime staff list for id: {}", id);
            return ResponseEntity.ok(staffList);
        } catch (Exception e) {
            logger.error("Error retrieving anime staff list for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving anime staff list: " + e.getMessage());
        }
    }

    @GetMapping("/anime-stats/{id}")
    public ResponseEntity<String> getAnimeStatistics(@PathVariable("id") String id) {
        logger.info("Fetching anime statistics for id: {}", id);
        try {
            String animeStatistics = animeService.getAnimeStatistics(id);
            logger.info("Successfully fetched anime statistics for id: {}", id);
            return ResponseEntity.ok(animeStatistics);
        } catch (Exception e) {
            logger.error("Error retrieving anime statistics for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving anime statistics: " + e.getMessage());
        }
    }

    @GetMapping("/recommendation/{id}")
    public ResponseEntity<String> getRecommendedAnime(@PathVariable("id") String id) {
        logger.info("Fetching recommended anime for id: {}", id);
        try {
            String recommendedAnime = animeService.getRecommendedAnime(id);

            return ResponseEntity.ok(recommendedAnime);
        } catch (Exception e) {
            logger.error("Error retrieving recommended anime for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving recommended anime: " + e.getMessage());
        }
    }

}