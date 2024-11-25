package com.review.anime.Dto;

import com.review.anime.dto.Token;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenTest {

    private Token token = new Token();
    @Test
    public void testGettersAndSetters() {
        Token token = new Token();

        token.setToken("12345abcde");

        assertEquals("12345abcde", token.getToken());
    }

    @Test
    public void testAllArgsConstructor() {
        Token token = new Token("12345abcde");

        assertEquals("12345abcde", token.getToken());
    }
}