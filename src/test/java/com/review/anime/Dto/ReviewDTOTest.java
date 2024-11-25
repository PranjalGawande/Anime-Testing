package com.review.anime.Dto;

import com.review.anime.dto.ReviewDTO;
import com.review.anime.entites.Review;
import com.review.anime.entites.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReviewDTOTest {

    private ReviewDTO reviewDTO = new ReviewDTO();
    @Test
    public void testGettersAndSetters() {
        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setAnimeId(202);
        reviewDTO.setRating(3.5f);
        reviewDTO.setComment("Good Anime!");
        reviewDTO.setName("John Doe");
        reviewDTO.setCommentId(15);

        assertEquals(Integer.valueOf(202), reviewDTO.getAnimeId());
        assertEquals(Float.valueOf(3.5f), reviewDTO.getRating());
        assertEquals("Good Anime!", reviewDTO.getComment());
        assertEquals("John Doe", reviewDTO.getName());
        assertEquals(Integer.valueOf(15), reviewDTO.getCommentId());
    }

    @Test
    public void testAllArgsConstructor() {
        ReviewDTO reviewDTO = new ReviewDTO(202, 3.5f, "Good Anime!", "John Doe", 15);

        assertEquals(Integer.valueOf(202), reviewDTO.getAnimeId());
        assertEquals(Float.valueOf(3.5f), reviewDTO.getRating());
        assertEquals("Good Anime!", reviewDTO.getComment());
        assertEquals("John Doe", reviewDTO.getName());
        assertEquals(Integer.valueOf(15), reviewDTO.getCommentId());
    }

    @Test
    public void testConstructorWithReviewEntity() {
        User user = new User();
        user.setName("John Doe");

        Review review = new Review(15, 202, 3.5f, "Good Anime!", user);
        ReviewDTO reviewDTO = new ReviewDTO(review);

        assertEquals(Integer.valueOf(202), reviewDTO.getAnimeId());
        assertEquals(Float.valueOf(3.5f), reviewDTO.getRating());
        assertEquals("Good Anime!", reviewDTO.getComment());
        assertEquals("John Doe", reviewDTO.getName());
        assertEquals(Integer.valueOf(15), reviewDTO.getCommentId());
    }
}