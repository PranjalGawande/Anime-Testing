//package com.review.anime.Integration;
//
//import com.review.anime.entites.Review;
//import com.review.anime.repository.ReviewRepository;
//import com.review.anime.service.ReviewService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class ReviewServiceIntegrationTest {
//
//    @InjectMocks
//    @Autowired
//    private ReviewService reviewService;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Test
//    void testSaveReview() {
//        Review review = new Review();
//        review.setAnimeId(1);
//        review.setComment("Great anime!");
//
//        reviewService.saveReview(review);
//
//        verify(reviewRepository, times(1)).save(review);
//    }
//
//    @Test
//    void testGetReviewOfAnimeId() {
//        Review review1 = new Review();
//        review1.setAnimeId(1);
//        review1.setComment("Amazing anime!");
//
//        when(reviewRepository.findAllByAnimeId(1)).thenReturn(List.of(review1));
//
//        List<Review> reviews = reviewService.getReviewOfAnimeId(1);
//
//        assert !reviews.isEmpty();
//    }
//
//}
