package com.review.anime.service;

import com.review.anime.entites.Review;
import com.review.anime.repository.ReviewRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private static final Logger logger = LogManager.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;
    public Review saveReview(Review review) {
        logger.info("Saving review for anime ID: {}", review.getAnimeId());
        try {
            reviewRepository.save(review);
            logger.info("Review saved successfully for anime ID: {}", review.getAnimeId());
        } catch (Exception e) {
            logger.error("Error saving review for anime ID: {}", review.getAnimeId(), e);
            throw e;
        }
        return review;
    }

    public List<Review> getReviewOfAnimeId(Integer animeId) {
        logger.info("Fetching reviews for anime ID: {}", animeId);
        try {
            List<Review> reviews = reviewRepository.findAllByAnimeId(animeId);
            logger.info("Successfully fetched {} reviews for anime ID: {}", reviews.size(), animeId);
            return reviews;
        } catch (Exception e) {
            logger.error("Error fetching reviews for anime ID: {}", animeId, e);
            throw e; // Re-throw the exception after logging it
        }
    }

    public void deleteComment(Integer commentId) {
        logger.info("Deleting comment with ID: {}", commentId);
        try {
            if (!reviewRepository.existsById(commentId)) {
                throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
            }
            reviewRepository.deleteById(commentId);
            logger.info("Successfully deleted comment with ID: {}", commentId);
        } catch (Exception e) {
            logger.error("Error deleting comment with ID: {}", commentId, e);
            throw e;
        }
    }
}
