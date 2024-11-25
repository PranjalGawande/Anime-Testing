package com.review.anime.Entites;

import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import org.junit.Test;

import static org.junit.Assert.*;

public class WatchListTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User();
        WatchList watchList = new WatchList();

        watchList.setWatchId(1);
        watchList.setAnimeId(101);
        watchList.setImageUrl("https://example.com/image.jpg");
        watchList.setTitle("Naruto");
        watchList.setUser(user);

        assertEquals(Integer.valueOf(1), watchList.getWatchId());
        assertEquals(Integer.valueOf(101), watchList.getAnimeId());
        assertEquals("https://example.com/image.jpg", watchList.getImageUrl());
        assertEquals("Naruto", watchList.getTitle());
        assertEquals(user, watchList.getUser());
    }

    @Test
    public void testConstructor() {
        User user = new User();
        WatchList watchList = new WatchList(1, 101, "https://example.com/image.jpg", "Naruto", user);

        assertEquals(Integer.valueOf(1), watchList.getWatchId());
        assertEquals(Integer.valueOf(101), watchList.getAnimeId());
        assertEquals("https://example.com/image.jpg", watchList.getImageUrl());
        assertEquals("Naruto", watchList.getTitle());
        assertEquals(user, watchList.getUser());
    }
}