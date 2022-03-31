package com.kereq.unit.main;

import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.CommentLikeData;
import com.kereq.main.entity.CommentStatisticsData;
import com.kereq.main.repository.CommentLikeRepository;
import com.kereq.main.repository.CommentRepository;
import com.kereq.main.repository.CommentStatisticsRepository;
import com.kereq.main.service.CommentStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CommentStatisticsServiceUnitTest {

    private final CommentLikeRepository commentLikeRepository = Mockito.mock(CommentLikeRepository.class);

    private final CommentStatisticsRepository commentStatisticsRepository = Mockito.mock(CommentStatisticsRepository.class);

    private CommentStatisticsService commentStatisticsService;

    @BeforeEach
    public void setup() {
        when(commentStatisticsRepository.save(Mockito.any(CommentStatisticsData.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(commentStatisticsRepository.findByCommentId(1L)).thenReturn(null);
        when(commentStatisticsRepository.findByCommentId(2L)).thenReturn(new CommentStatisticsData());
        commentStatisticsService = new CommentStatisticsService(commentLikeRepository, commentStatisticsRepository);
    }

    @Test
    void testFillUserLikeType() {
        CommentStatisticsData commentStatistics = new CommentStatisticsData();

        CommentLikeData commentLike = new CommentLikeData();
        commentLike.setType(LikeType.LIKE);
        when(commentLikeRepository.findByUserIdAndCommentId(1L, 4L)).thenReturn(commentLike);

        CommentLikeData commentDislike = new CommentLikeData();
        commentDislike.setType(LikeType.DISLIKE);
        when(commentLikeRepository.findByUserIdAndCommentId(1L, 5L)).thenReturn(commentDislike);

        commentStatistics.setCommentId(3L);
        commentStatisticsService.fillUserLikeType(1L, commentStatistics);
        assertThat(commentStatistics.getUserLikeType()).isNull();

        commentStatistics.setCommentId(4L);
        commentStatisticsService.fillUserLikeType(1L, commentStatistics);
        assertThat(commentStatistics.getUserLikeType()).isEqualTo(LikeType.LIKE);

        commentStatistics.setCommentId(5L);
        commentStatisticsService.fillUserLikeType(1L, commentStatistics);
        assertThat(commentStatistics.getUserLikeType()).isEqualTo(LikeType.DISLIKE);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.fillUserLikeType(1L, null));
    }

    @Test
    void testInitialize() {
        CommentStatisticsData commentStatistics = commentStatisticsService.initialize();
        assertThat(commentStatistics.getLikesCount()).isZero();
        assertThat(commentStatistics.getDislikesCount()).isZero();
        assertThat(commentStatistics.getUserLikeType()).isNull();
    }

    @Test
    void testConvertLike() {
        final int startLikesCount = 1;
        final int startDislikesCount = 1;
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setLikesCount(startLikesCount);
        commentStatistics.setDislikesCount(startDislikesCount);
        when(commentStatisticsRepository.findByCommentId(3L)).thenReturn(commentStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.convertLike(1L, true));
        commentStatisticsService.convertLike(3L, true);
        assertThat(commentStatistics.getLikesCount()).isEqualTo(startLikesCount + 1);
        assertThat(commentStatistics.getDislikesCount()).isEqualTo(startDislikesCount - 1);

        commentStatisticsService.convertLike(3L, false);
        assertThat(commentStatistics.getLikesCount()).isEqualTo(startLikesCount);
        assertThat(commentStatistics.getDislikesCount()).isEqualTo(startDislikesCount);
    }

    @Test
    void testAddLike() {
        final int startLikesCount = 1;
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setLikesCount(startLikesCount);
        when(commentStatisticsRepository.findByCommentId(3L)).thenReturn(commentStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.addLike(1L));
        commentStatisticsService.addLike(3L);
        assertThat(commentStatistics.getLikesCount()).isEqualTo(startLikesCount + 1);
    }

    @Test
    void testRemoveLike() {
        final int startLikesCount = 1;
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setLikesCount(startLikesCount);
        when(commentStatisticsRepository.findByCommentId(3L)).thenReturn(commentStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.addLike(1L));
        commentStatisticsService.removeLike(3L);
        assertThat(commentStatistics.getLikesCount()).isEqualTo(startLikesCount - 1);
    }

    @Test
    void testAddDislike() {
        final int startDislikesCount = 1;
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setDislikesCount(startDislikesCount);
        when(commentStatisticsRepository.findByCommentId(3L)).thenReturn(commentStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.addDislike(1L));
        commentStatisticsService.addDislike(3L);
        assertThat(commentStatistics.getDislikesCount()).isEqualTo(startDislikesCount + 1);
    }

    @Test
    void testRemoveDislike() {
        final int startDislikesCount = 1;
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setDislikesCount(startDislikesCount);
        when(commentStatisticsRepository.findByCommentId(3L)).thenReturn(commentStatistics);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.removeDislike(1L));
        commentStatisticsService.removeDislike(3L);
        assertThat(commentStatistics.getDislikesCount()).isEqualTo(startDislikesCount - 1);
    }
}
