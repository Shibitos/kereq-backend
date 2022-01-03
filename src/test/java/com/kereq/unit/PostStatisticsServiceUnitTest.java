package com.kereq.unit;

import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.PostLikeData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.repository.PostLikeRepository;
import com.kereq.main.repository.PostRepository;
import com.kereq.main.repository.PostStatisticsRepository;
import com.kereq.main.service.PostStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class PostStatisticsServiceUnitTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostStatisticsRepository postStatisticsRepository;

    @InjectMocks
    private PostStatisticsService postStatisticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(postStatisticsRepository.save(Mockito.any(PostStatisticsData.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(postStatisticsRepository.findByPostId(1L)).thenReturn(null);
        when(postStatisticsRepository.findByPostId(2L)).thenReturn(new PostStatisticsData());
    }

    @Test
    void testGetStatistics() {
        PostStatisticsData postStatistics = new PostStatisticsData();
        when(postStatisticsRepository.findByPostId(3L)).thenReturn(postStatistics);
        when(postStatisticsRepository.findByPostId(4L)).thenReturn(postStatistics);

        PostLikeData postLike = new PostLikeData();
        postLike.setType(LikeType.LIKE);
        when(postLikeRepository.findByUserIdAndPostId(1L, 4L)).thenReturn(postLike);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.getStatistics(1L, 1L));
        postStatisticsService.getStatistics(1L, 3L);
        assertThat(postStatistics.getUserLikeType()).isNull();
        postStatisticsService.getStatistics(1L, 4L);
        assertThat(postStatistics.getUserLikeType()).isEqualTo(LikeType.LIKE);
    }

    @Test
    void testInitialize() {
        when(postStatisticsRepository.existsByPostId(1L)).thenReturn(true);
        when(postStatisticsRepository.existsByPostId(2L)).thenReturn(false);

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> postStatisticsService.initialize(1L));
        PostStatisticsData postStatistics =
                postStatisticsService.initialize(2L);
        assertThat(postStatistics.getPostId()).isEqualTo(2L);
        assertThat(postStatistics.getLikesCount()).isZero();
        assertThat(postStatistics.getDislikesCount()).isZero();
        assertThat(postStatistics.getUserLikeType()).isNull();
    }

    @Test
    void testRemove() {
        when(postStatisticsRepository.existsByPostId(1L)).thenReturn(true);
        when(postStatisticsRepository.existsByPostId(2L)).thenReturn(false);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.remove(1L));
        postStatisticsService.remove(2L);
        Mockito.verify(postStatisticsRepository, times(1))
                .delete(Mockito.any(PostStatisticsData.class));
    }

    @Test
    void testConvertLike() {
        final int startLikesCount = 1;
        final int startDislikesCount = 1;
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setLikesCount(startLikesCount);
        postStatistics.setDislikesCount(startDislikesCount);
        when(postStatisticsRepository.findByPostId(3L)).thenReturn(postStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.convertLike(1L, true));
        postStatisticsService.convertLike(3L, true);
        assertThat(postStatistics.getLikesCount()).isEqualTo(startLikesCount + 1);
        assertThat(postStatistics.getDislikesCount()).isEqualTo(startDislikesCount - 1);

        postStatisticsService.convertLike(3L, false);
        assertThat(postStatistics.getLikesCount()).isEqualTo(startLikesCount);
        assertThat(postStatistics.getDislikesCount()).isEqualTo(startDislikesCount);
    }

    @Test
    void testAddLike() {
        final int startLikesCount = 1;
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setLikesCount(startLikesCount);
        when(postStatisticsRepository.findByPostId(3L)).thenReturn(postStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.addLike(1L));
        postStatisticsService.addLike(3L);
        assertThat(postStatistics.getLikesCount()).isEqualTo(startLikesCount + 1);
    }

    @Test
    void testRemoveLike() {
        final int startLikesCount = 1;
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setLikesCount(startLikesCount);
        when(postStatisticsRepository.findByPostId(3L)).thenReturn(postStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.addLike(1L));
        postStatisticsService.removeLike(3L);
        assertThat(postStatistics.getLikesCount()).isEqualTo(startLikesCount - 1);
    }

    @Test
    void testAddDislike() {
        final int startDislikesCount = 1;
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setDislikesCount(startDislikesCount);
        when(postStatisticsRepository.findByPostId(3L)).thenReturn(postStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.addDislike(1L));
        postStatisticsService.addDislike(3L);
        assertThat(postStatistics.getDislikesCount()).isEqualTo(startDislikesCount + 1);
    }

    @Test
    void testRemoveDislike() {
        final int startDislikesCount = 1;
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setDislikesCount(startDislikesCount);
        when(postStatisticsRepository.findByPostId(3L)).thenReturn(postStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.removeDislike(1L));
        postStatisticsService.removeDislike(3L);
        assertThat(postStatistics.getDislikesCount()).isEqualTo(startDislikesCount - 1);
    }
}
