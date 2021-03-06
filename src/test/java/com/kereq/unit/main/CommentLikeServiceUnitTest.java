package com.kereq.unit.main;

import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.CommentLikeData;
import com.kereq.main.repository.CommentLikeRepository;
import com.kereq.main.service.CommentLikeService;
import com.kereq.main.service.CommentStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class CommentLikeServiceUnitTest {

    private final CommentLikeRepository commentLikeRepository = Mockito.mock(CommentLikeRepository.class);

    private final CommentStatisticsService commentStatisticsService = Mockito.mock(CommentStatisticsService.class);

    private CommentLikeService commentLikeService;

    @BeforeEach
    public void setup() {
        when(commentLikeRepository.findByUserIdAndCommentId(1L, 1L)).thenReturn(null);
        when(commentLikeRepository.findByUserIdAndCommentId(2L, 1L))
                .thenReturn(buildCommentLike(LikeType.LIKE));
        when(commentLikeRepository.findByUserIdAndCommentId(3L, 1L))
                .thenReturn(buildCommentLike(LikeType.DISLIKE));
        commentLikeService = new CommentLikeService(commentLikeRepository, commentStatisticsService);
    }

    @Test
    void testAddLike() {
        commentLikeService.addLike(1L, 1L);
        Mockito.verify(commentLikeRepository, times(1)).save(Mockito.any(CommentLikeData.class));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> commentLikeService.addLike(2L, 1L));

        commentLikeService.addLike(3L, 1L);
        Mockito.verify(commentLikeRepository, times(2)).save(Mockito.any(CommentLikeData.class));
    }

    @Test
    void testRemoveLike() {
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeLike(1L, 1L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeLike(3L, 1L));

        commentLikeService.removeLike(2L, 1L);
        Mockito.verify(commentLikeRepository, times(1))
                .delete(Mockito.any(CommentLikeData.class));
        Mockito.verify(commentStatisticsService, times(1)).removeLike(1L);
    }
    
    @Test
    void testAddDislike() {
        commentLikeService.addDislike(1L, 1L);
        Mockito.verify(commentLikeRepository, times(1)).save(Mockito.any(CommentLikeData.class));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> commentLikeService.addDislike(3L, 1L));

        commentLikeService.addDislike(2L, 1L);
        Mockito.verify(commentLikeRepository, times(2)).save(Mockito.any(CommentLikeData.class));
    }

    @Test
    void testRemoveDislike() {
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeDislike(1L, 1L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeDislike(2L, 1L));

        commentLikeService.removeDislike(3L, 1L);
        Mockito.verify(commentLikeRepository, times(1))
                .delete(Mockito.any(CommentLikeData.class));
        Mockito.verify(commentStatisticsService, times(1)).removeDislike(1L);
    }

    private CommentLikeData buildCommentLike(int type) {
        CommentLikeData commentLike = new CommentLikeData();
        commentLike.setType(type);
        return commentLike;
    }
}
