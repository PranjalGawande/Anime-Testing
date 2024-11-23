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

        doNothing().when(reviewRepository).deleteById(commentId);

        reviewService.deleteComment(commentId);

        verify(reviewRepository, times(1)).deleteById(commentId);
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
        Integer commentId = 1;
        doThrow(new RuntimeException("Database error")).when(reviewRepository).deleteById(commentId);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteComment(commentId);
        });

        assertEquals("Database error", exception.getMessage());
        verify(reviewRepository, times(1)).deleteById(commentId);
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