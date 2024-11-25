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
        // Preparing a test review
        testReview = new Review();
        testReview.setAnimeId(1);
        testReview.setComment("Great anime!");
        testReview.setRating(5.0f);

        // Clean repository before testing
        reviewRepository.deleteAll();
    }

    @Test
    @DisplayName("Test saving a review successfully")
    void testSaveReview() {
        // Act
        Review savedReview = reviewService.saveReview(testReview);

        // Assert
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getCommentId()).isNotNull(); // ID should be generated
        assertThat(savedReview.getAnimeId()).isEqualTo(testReview.getAnimeId());
        assertThat(savedReview.getComment()).isEqualTo(testReview.getComment());
        assertThat(savedReview.getRating()).isEqualTo(testReview.getRating());
    }

    @Test
    @DisplayName("Test saving a null review throws exception")
    void testSaveReviewThrowsException() {
        // Assert
        assertThrows(Exception.class, () -> reviewService.saveReview(null));
    }

    @Test
    @DisplayName("Test fetching reviews by anime ID")
    void testGetReviewOfAnimeId() {
        // Arrange
        Review savedReview = reviewService.saveReview(testReview);

        // Act
        List<Review> reviews = reviewService.getReviewOfAnimeId(savedReview.getAnimeId());

        // Assert
        assertThat(reviews).isNotEmpty();
        assertThat(reviews.get(0).getAnimeId()).isEqualTo(savedReview.getAnimeId());
        assertThat(reviews.get(0).getComment()).isEqualTo(savedReview.getComment());
    }

    @Test
    @DisplayName("Test fetching reviews for non-existent anime ID returns empty list")
    void testGetReviewOfNonExistentAnimeId() {
        // Act
        List<Review> reviews = reviewService.getReviewOfAnimeId(999); // Non-existent anime ID

        // Assert
        assertThat(reviews).isEmpty();
    }

    @Test
    @DisplayName("Test deleting a comment successfully")
    void testDeleteComment() {
        // Arrange
        Review savedReview = reviewService.saveReview(testReview);
        Integer reviewId = savedReview.getCommentId();

        // Act
        reviewService.deleteComment(reviewId);

        // Assert
        Optional<Review> deletedReview = reviewRepository.findById(reviewId);
        assertThat(deletedReview).isEmpty(); // The review should be deleted
    }

    @Test
    @DisplayName("Test deleting a non-existent comment throws exception")
    void testDeleteNonExistentComment() {
        // Assert
        assertThrows(Exception.class, () -> reviewService.deleteComment(9999)); // Non-existent ID
    }

    @AfterAll
    void cleanup() {
        // Clean up the repository after all tests
        reviewRepository.deleteAll();
    }
}
