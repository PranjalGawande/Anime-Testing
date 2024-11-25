package com.review.anime.Service;

import com.review.anime.entites.Review;
import com.review.anime.repository.ReviewRepository;
import com.review.anime.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceUnitTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReview() {
        Review review = new Review(null, 1, 4.5f, "Great anime!", null);
        when(reviewRepository.save(any())).thenReturn(review);

        Review result = reviewService.saveReview(review);

        assertNotNull(result);
        assertEquals(4.5f, result.getRating());
        verify(reviewRepository, times(1)).save(any());
    }

    @Test
    void testGetReviewOfAnimeIdWithNoReviews() {
        Integer animeId = 1;
        when(reviewRepository.findAllByAnimeId(animeId)).thenReturn(Collections.emptyList());

        List<Review> reviews = reviewService.getReviewOfAnimeId(animeId);

        assertNotNull(reviews);
        assertEquals(0, reviews.size());
        verify(reviewRepository, times(1)).findAllByAnimeId(animeId);
    }

    @Test
    void testDeleteComment() {
        Integer commentId = 1;

        // Mock the repository to return Optional.empty() when trying to find the comment
        when(reviewRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        // Expecting IllegalArgumentException because the comment does not exist
        assertThrows(IllegalArgumentException.class, () -> reviewService.deleteComment(commentId));

        // Verify that deleteById was not called since the comment does not exist
        verify(reviewRepository, times(0)).deleteById(commentId);
    }


    @Test
    void testSaveReviewThrowsException() {
        Review review = new Review(null, 1, 4.5f, "Great anime!", null);
        when(reviewRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.saveReview(review);
        });

        assertEquals("Database error", exception.getMessage());
        verify(reviewRepository, times(1)).save(any());
    }
    @Test
    void testDeleteCommentThrowsException() {
        // Arrange
        Integer nonExistentCommentId = 1; // or any ID you know doesn't exist

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.deleteComment(nonExistentCommentId),
                "Expected deleteComment() to throw, but it didn't"
        );

        // Verify the exception message
        assertEquals("Comment with ID 1 does not exist.", thrown.getMessage());
    }


    @Test
    void testGetReviewOfAnimeIdWithReviews() {
        Integer animeId = 1;
        List<Review> mockReviews = List.of(new Review(1, animeId, 4.5f, "Great!", null));
        when(reviewRepository.findAllByAnimeId(animeId)).thenReturn(mockReviews);

        List<Review> reviews = reviewService.getReviewOfAnimeId(animeId);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(4.5f, reviews.get(0).getRating());
        verify(reviewRepository, times(1)).findAllByAnimeId(animeId);
    }

}