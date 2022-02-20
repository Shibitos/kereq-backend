package com.kereq.unit.main;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.service.EnvironmentService;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.CommentData;
import com.kereq.main.entity.PostData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.PostRepository;
import com.kereq.main.service.CommentService;
import com.kereq.main.service.ImageService;
import com.kereq.main.service.PostService;
import com.kereq.main.service.PostStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
