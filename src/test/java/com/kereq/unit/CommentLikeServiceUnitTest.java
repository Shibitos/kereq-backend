package com.kereq.unit;

import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.CommentLikeData;
import com.kereq.main.repository.CommentLikeRepository;
import com.kereq.main.service.CommentLikeService;
import com.kereq.main.service.CommentStatisticsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class CommentLikeServiceUnitTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentStatisticsService commentStatisticsService;

    @InjectMocks
    private CommentLikeService commentLikeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(commentLikeRepository.findByUserIdAndCommentId(1L, 1L)).thenReturn(null);
        when(commentLikeRepository.findByUserIdAndCommentId(2L, 1L))
                .thenReturn(buildCommentLike(LikeType.LIKE));
        when(commentLikeRepository.findByUserIdAndCommentId(3L, 1L))
                .thenReturn(buildCommentLike(LikeType.DISLIKE));
    }

    @Test
    void testAddLike() {
        Assertions.assertDoesNotThrow(() -> commentLikeService.addLike(1L, 1L));
        Mockito.verify(commentLikeRepository, times(1)).save(Mockito.any(CommentLikeData.class));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> commentLikeService.addLike(2L, 1L));

        Assertions.assertDoesNotThrow(() -> commentLikeService.addLike(3L, 1L));
        Mockito.verify(commentLikeRepository, times(2)).save(Mockito.any(CommentLikeData.class));
    }

    @Test
    void testRemoveLike() {
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeLike(1L, 1L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeLike(3L, 1L));

        Assertions.assertDoesNotThrow(() -> commentLikeService.removeLike(2L, 1L));
        Mockito.verify(commentLikeRepository, times(1))
                .delete(Mockito.any(CommentLikeData.class));
        Mockito.verify(commentStatisticsService, times(1)).removeLike(1L);
    }
    
    @Test
    void testAddDislike() {
        Assertions.assertDoesNotThrow(() -> commentLikeService.addDislike(1L, 1L));
        Mockito.verify(commentLikeRepository, times(1)).save(Mockito.any(CommentLikeData.class));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> commentLikeService.addDislike(3L, 1L));

        Assertions.assertDoesNotThrow(() -> commentLikeService.addDislike(2L, 1L));
        Mockito.verify(commentLikeRepository, times(2)).save(Mockito.any(CommentLikeData.class));
    }

    @Test
    void testRemoveDislike() {
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeDislike(1L, 1L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentLikeService.removeDislike(2L, 1L));

        Assertions.assertDoesNotThrow(() -> commentLikeService.removeDislike(3L, 1L));
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
