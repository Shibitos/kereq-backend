package com.kereq.unit.main;

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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.parameters.P;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PostStatisticsServiceUnitTest {


    private final PostRepository postRepository = Mockito.mock(PostRepository.class);

    private final PostLikeRepository postLikeRepository = Mockito.mock(PostLikeRepository.class);

    private final PostStatisticsRepository postStatisticsRepository = Mockito.mock(PostStatisticsRepository.class);

    private PostStatisticsService postStatisticsService;

    @BeforeEach
    public void setup() {
        when(postStatisticsRepository.save(Mockito.any(PostStatisticsData.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(postStatisticsRepository.findByPostId(1L)).thenReturn(null);
        when(postStatisticsRepository.findByPostId(2L)).thenReturn(new PostStatisticsData());
        postStatisticsService = new PostStatisticsService(postRepository, postLikeRepository, postStatisticsRepository);
    }
    
    @Test
    void testFillUserLikeType() {
        PostStatisticsData postStatistics = new PostStatisticsData();

        PostLikeData postLike = new PostLikeData();
        postLike.setType(LikeType.LIKE);
        when(postLikeRepository.findByUserIdAndPostId(1L, 4L)).thenReturn(postLike);

        PostLikeData postDislike = new PostLikeData();
        postDislike.setType(LikeType.DISLIKE);
        when(postLikeRepository.findByUserIdAndPostId(1L, 5L)).thenReturn(postDislike);

        postStatistics.setPostId(3L);
        postStatisticsService.fillUserLikeType(1L, postStatistics);
        assertThat(postStatistics.getUserLikeType()).isNull();

        postStatistics.setPostId(4L);
        postStatisticsService.fillUserLikeType(1L, postStatistics);
        assertThat(postStatistics.getUserLikeType()).isEqualTo(LikeType.LIKE);

        postStatistics.setPostId(5L);
        postStatisticsService.fillUserLikeType(1L, postStatistics);
        assertThat(postStatistics.getUserLikeType()).isEqualTo(LikeType.DISLIKE);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postStatisticsService.fillUserLikeType(1L, null));
    }

    @Test
    void testInitialize() {
        PostStatisticsData postStatistics = postStatisticsService.initialize();
        assertThat(postStatistics.getLikesCount()).isZero();
        assertThat(postStatistics.getDislikesCount()).isZero();
        assertThat(postStatistics.getUserLikeType()).isNull();
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
