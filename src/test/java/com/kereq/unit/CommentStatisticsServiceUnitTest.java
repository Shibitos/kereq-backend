package com.kereq.unit;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class CommentStatisticsServiceUnitTest {


    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentStatisticsRepository commentStatisticsRepository;

    @InjectMocks
    private CommentStatisticsService commentStatisticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(commentStatisticsRepository.save(Mockito.any(CommentStatisticsData.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(commentStatisticsRepository.findByCommentId(1L)).thenReturn(null);
        when(commentStatisticsRepository.findByCommentId(2L)).thenReturn(new CommentStatisticsData());
    }

    @Test
    void testGetStatistics() {
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        when(commentStatisticsRepository.findByCommentId(3L)).thenReturn(commentStatistics);
        when(commentStatisticsRepository.findByCommentId(4L)).thenReturn(commentStatistics);

        CommentLikeData commentLike = new CommentLikeData();
        commentLike.setType(LikeType.LIKE);
        when(commentLikeRepository.findByUserIdAndCommentId(1L, 4L)).thenReturn(commentLike);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.getStatistics(1L, 1L));
        commentStatisticsService.getStatistics(1L, 3L);
        assertThat(commentStatistics.getUserLikeType()).isNull();
        commentStatisticsService.getStatistics(1L, 4L);
        assertThat(commentStatistics.getUserLikeType()).isEqualTo(LikeType.LIKE);
    }

    @Test
    void testInitialize() {
        when(commentStatisticsRepository.existsByCommentId(1L)).thenReturn(true);
        when(commentStatisticsRepository.existsByCommentId(2L)).thenReturn(false);

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> commentStatisticsService.initialize(1L));
        CommentStatisticsData commentStatistics =
                commentStatisticsService.initialize(2L);
        assertThat(commentStatistics.getCommentId()).isEqualTo(2L);
        assertThat(commentStatistics.getLikesCount()).isZero();
        assertThat(commentStatistics.getDislikesCount()).isZero();
        assertThat(commentStatistics.getUserLikeType()).isNull();
    }

    @Test
    void testRemove() {
        when(commentStatisticsRepository.existsByCommentId(1L)).thenReturn(true);
        when(commentStatisticsRepository.existsByCommentId(2L)).thenReturn(false);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentStatisticsService.remove(1L));
        commentStatisticsService.remove(2L);
        Mockito.verify(commentStatisticsRepository, times(1))
                .delete(Mockito.any(CommentStatisticsData.class));
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
