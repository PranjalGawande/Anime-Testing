package com.review.anime.Dto;

import com.review.anime.dto.ExtraDTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExtraDTOTest {
    private ExtraDTO extraDTO = new ExtraDTO();
    @Test
    public void testGettersAndSetters() {
        ExtraDTO extraDTO = new ExtraDTO();

        extraDTO.setAnimeId(101);
        extraDTO.setRating(4.8f);
        extraDTO.setComment("Great Anime!");
        extraDTO.setNewPassword("newPass123");
        extraDTO.setOldPassword("oldPass456");

        assertEquals(Integer.valueOf(101), extraDTO.getAnimeId());
        assertEquals(Float.valueOf(4.8f), extraDTO.getRating());
        assertEquals("Great Anime!", extraDTO.getComment());
        assertEquals("newPass123", extraDTO.getNewPassword());
        assertEquals("oldPass456", extraDTO.getOldPassword());
    }

    @Test
    public void testAllArgsConstructor() {
        ExtraDTO extraDTO = new ExtraDTO(101, 4.8f, "Great Anime!", "newPass123", "oldPass456");

        assertEquals(Integer.valueOf(101), extraDTO.getAnimeId());
        assertEquals(Float.valueOf(4.8f), extraDTO.getRating());
        assertEquals("Great Anime!", extraDTO.getComment());
        assertEquals("newPass123", extraDTO.getNewPassword());
        assertEquals("oldPass456", extraDTO.getOldPassword());
    }
}