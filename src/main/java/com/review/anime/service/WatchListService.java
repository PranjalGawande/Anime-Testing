package com.review.anime.service;

import com.review.anime.entites.WatchList;
import com.review.anime.repository.WatchListRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WatchListService {

    private static final Logger logger = LogManager.getLogger(WatchListService.class);

    @Autowired
    private WatchListRepository watchListRepository;

    public void addWatchAnime(WatchList watchList1) {
        if (watchList1 == null) {
            throw new NullPointerException("WatchList object cannot be null");
        }

        // Validate animeId
        if (watchList1.getAnimeId() == null || watchList1.getAnimeId() <= 0) {
            throw new IllegalArgumentException("Invalid animeId");
        }

        // Validate URL (non-empty)
//        if (watchList1.getUrl() == null || watchList1.getUrl().trim().isEmpty()) {
//            throw new IllegalArgumentException("URL cannot be empty");
//        }

        // Validate title (non-empty)
        if (watchList1.getTitle() == null || watchList1.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        // Validate user (non-null)
        if (watchList1.getUser() == null) {
            throw new NullPointerException("User cannot be null");
        }

        logger.info("Adding anime to watch list with ID: {}", watchList1.getAnimeId());
        try {
            watchListRepository.save(watchList1);
            logger.info("Successfully added anime to watch list with ID: {}", watchList1.getAnimeId());
        } catch (Exception e) {
            logger.error("Error adding anime to watch list with ID: {}", watchList1.getAnimeId(), e);
            throw e;
        }
    }
}
