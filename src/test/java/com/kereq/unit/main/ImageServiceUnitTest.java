package com.kereq.unit.main;

import com.kereq.common.service.EnvironmentService;
import com.kereq.main.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ImageServiceUnitTest {

    @Mock
    private EnvironmentService environmentService;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreatePost() {
    }

}
