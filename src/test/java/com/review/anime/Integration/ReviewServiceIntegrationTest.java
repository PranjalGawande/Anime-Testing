package com.review.anime.Integration;

import com.review.anime.entites.Review;
import com.review.anime.repository.ReviewRepository;
import com.review.anime.service.ReviewService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewServiceIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    private Review testReview;

    @BeforeAll
    void setup() {
        testReview = new Review();
        testReview.setAnimeId(1);
        testReview.setComment("Great anime!");
        testReview.setRating(5.0f);
        reviewRepository.deleteAll();
    }

    // Save Review Tests
    @Test
    void saveValidReview() {
        Review savedReview = reviewService.saveReview(testReview);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getCommentId()).isNotNull();
        assertThat(savedReview.getAnimeId()).isEqualTo(testReview.getAnimeId());
        assertThat(savedReview.getComment()).isEqualTo(testReview.getComment());
        assertThat(savedReview.getRating()).isEqualTo(testReview.getRating());
    }

    @Test
    void saveInvalidReview() {
        assertThrows(Exception.class, () -> reviewService.saveReview(null));
    }

    // Fetch Review Tests
    @Test
    void getReviews() {
        Review savedReview = reviewService.saveReview(testReview);

        List<Review> reviews = reviewService.getReviewOfAnimeId(savedReview.getAnimeId());

        assertThat(reviews).isNotEmpty();
        assertThat(reviews.get(0).getAnimeId()).isEqualTo(savedReview.getAnimeId());
        assertThat(reviews.get(0).getComment()).isEqualTo(savedReview.getComment());
    }

    @Test
    void getReviewsNotFound() {
        List<Review> reviews = reviewService.getReviewOfAnimeId(999);
        assertThat(reviews).isEmpty();
    }

    // Delete Review Tests
    @Test
    void deleteReview() {
        Review savedReview = reviewService.saveReview(testReview);
        Integer reviewId = savedReview.getCommentId();

        reviewService.deleteComment(reviewId);

        Optional<Review> deletedReview = reviewRepository.findById(reviewId);
        assertThat(deletedReview).isEmpty();
    }

    @Test
    void deleteInvalidReview() {
        assertThrows(Exception.class, () -> reviewService.deleteComment(9999));
    }

    @AfterAll
    void cleanup() {
        reviewRepository.deleteAll();
    }
}