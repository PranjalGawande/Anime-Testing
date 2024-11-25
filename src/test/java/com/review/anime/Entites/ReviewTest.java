package com.review.anime.Entites;

import com.review.anime.entites.Review;
import com.review.anime.entites.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReviewTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User();
        Review review = new Review();

        review.setCommentId(1);
        review.setAnimeId(101);
        review.setRating(4.5f);
        review.setComment("Amazing anime!");
        review.setUser(user);

        assertEquals(Integer.valueOf(1), review.getCommentId());
        assertEquals(Integer.valueOf(101), review.getAnimeId());
        assertEquals(Float.valueOf(4.5f), review.getRating());
        assertEquals("Amazing anime!", review.getComment());
        assertEquals(user, review.getUser());
    }

    @Test
    public void testConstructor() {
        User user = new User();
        Review review = new Review(1, 101, 4.5f, "Amazing anime!", user);

        assertEquals(Integer.valueOf(1), review.getCommentId());
        assertEquals(Integer.valueOf(101), review.getAnimeId());
        assertEquals(Float.valueOf(4.5f), review.getRating());
        assertEquals("Amazing anime!", review.getComment());
        assertEquals(user, review.getUser());
    }
}