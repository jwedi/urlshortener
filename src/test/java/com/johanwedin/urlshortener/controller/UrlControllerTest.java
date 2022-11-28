package com.johanwedin.urlshortener.controller;

import com.johanwedin.urlshortener.db.UrlRepository;
import com.johanwedin.urlshortener.db.sentinels.CollisionException;
import com.johanwedin.urlshortener.helpers.hashing.HashStrategy;
import com.johanwedin.urlshortener.models.UrlMapping;
import com.johanwedin.urlshortener.models.sentinels.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class UrlControllerTest {

    private HashStrategy hs;
    private UrlRepository repository;
    private UrlController controller;

    @BeforeEach
    void setUp() {
        hs = mock(HashStrategy.class);
        repository = mock(UrlRepository.class);
        controller = new UrlController(repository, hs);
    }

    @Test
    void getByUrlId_shouldThrowNotFoundOnEmptyRepoResponse() {
        String urlId = "123456";
        when(repository.getByUrlId(urlId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> controller.getByUrlId(urlId));
    }

    @Test
    void createUrlMapping_shouldRetryWithPaddingOnCollision() {
        // Given
        String urlId1 = "12345";
        String urlId2 = "54321";
        String originalUrl = "http://google.com/asd";
        UrlMapping mapping = new UrlMapping(urlId1, originalUrl);
        UrlMapping mapping2 = new UrlMapping(urlId2, originalUrl);
        when(hs.hash(originalUrl, 0)).thenReturn(urlId1);
        when(hs.hash(originalUrl, 1)).thenReturn(urlId2);
        when(repository.addMapping(mapping)).thenThrow(CollisionException.class);
        when(repository.addMapping(mapping2)).thenReturn(mapping2);

        // When
        UrlMapping resultMapping = controller.createUrlMapping(originalUrl);

        // Then
        assertEquals(mapping2, resultMapping);
        verify(repository, VerificationModeFactory.times(1)).addMapping(mapping);
        verify(repository, VerificationModeFactory.times(1)).addMapping(mapping2);
    }
}