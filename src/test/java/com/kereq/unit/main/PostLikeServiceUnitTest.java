package com.kereq.unit.main;

import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.PostLikeData;
import com.kereq.main.repository.PostLikeRepository;
import com.kereq.main.service.PostLikeService;
import com.kereq.main.service.PostStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class PostLikeServiceUnitTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostStatisticsService postStatisticsService;

    @InjectMocks
    private PostLikeService postLikeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(postLikeRepository.findByUserIdAndPostId(1L, 1L)).thenReturn(null);
        when(postLikeRepository.findByUserIdAndPostId(2L, 1L))
                .thenReturn(buildPostLike(LikeType.LIKE));
        when(postLikeRepository.findByUserIdAndPostId(3L, 1L))
                .thenReturn(buildPostLike(LikeType.DISLIKE));
    }

    @Test
    void testAddLike() {
        postLikeService.addLike(1L, 1L);
        Mockito.verify(postLikeRepository, times(1)).save(Mockito.any(PostLikeData.class));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> postLikeService.addLike(2L, 1L));

        postLikeService.addLike(3L, 1L);
        Mockito.verify(postLikeRepository, times(2)).save(Mockito.any(PostLikeData.class));
    }

    @Test
    void testRemoveLike() {
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postLikeService.removeLike(1L, 1L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postLikeService.removeLike(3L, 1L));

        postLikeService.removeLike(2L, 1L);
        Mockito.verify(postLikeRepository, times(1))
                .delete(Mockito.any(PostLikeData.class));
        Mockito.verify(postStatisticsService, times(1)).removeLike(1L);
    }
    
    @Test
    void testAddDislike() {
        postLikeService.addDislike(1L, 1L);
        Mockito.verify(postLikeRepository, times(1)).save(Mockito.any(PostLikeData.class));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> postLikeService.addDislike(3L, 1L));

        postLikeService.addDislike(2L, 1L);
        Mockito.verify(postLikeRepository, times(2)).save(Mockito.any(PostLikeData.class));
    }

    @Test
    void testRemoveDislike() {
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postLikeService.removeDislike(1L, 1L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postLikeService.removeDislike(2L, 1L));

        postLikeService.removeDislike(3L, 1L);
        Mockito.verify(postLikeRepository, times(1))
                .delete(Mockito.any(PostLikeData.class));
        Mockito.verify(postStatisticsService, times(1)).removeDislike(1L);
    }

    private PostLikeData buildPostLike(int type) {
        PostLikeData postLike = new PostLikeData();
        postLike.setType(type);
        return postLike;
    }
}
