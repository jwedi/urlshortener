package com.johanwedin.urlshortener.helpers;

import com.johanwedin.urlshortener.helpers.hashing.MurmurHashStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MurmurHashStrategyTest {
    MurmurHashStrategy hashStrategy;
    @BeforeEach
    void setUp() {
        hashStrategy = new MurmurHashStrategy();
    }

    @Test
    void hash_shouldProduceDifferentHashesWhenPadded() {
        // Given
        String candidate = "https://www.youtube.com/asd";

        //When
        String res = hashStrategy.hash(candidate, 0);
        String resWithPadding = hashStrategy.hash(candidate, 1);

        // Then
        assertNotEquals(res, resWithPadding);
    }

    @Test
    void hash_shouldProduceShorterAlphaNumeric() {
        // Given
        String candidate = "https://www.youtube.com/asd";

        //When
        String res = hashStrategy.hash(candidate, 0);

        // Then
        assertTrue(candidate.length() > res.length());
        String isOnlyAlphaNumericRegex = "^[a-zA-Z0-9]*$";
        assertTrue(res.matches(isOnlyAlphaNumericRegex));
    }
}