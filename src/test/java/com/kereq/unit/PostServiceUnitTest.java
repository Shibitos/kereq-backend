package com.kereq.unit;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.CommentData;
import com.kereq.main.entity.PostData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.PostRepository;
import com.kereq.main.service.CommentService;
import com.kereq.main.service.PostService;
import com.kereq.main.service.PostStatisticsService;
import com.kereq.main.service.PostStatisticsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class PostServiceUnitTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostStatisticsService postStatisticsService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(postStatisticsService.initialize(Mockito.any())).thenReturn(new PostStatisticsData());
        when(postStatisticsService.getStatistics(Mockito.any(), Mockito.any()))
                .thenReturn(new PostStatisticsData());
        when(postRepository.save(Mockito.any(PostData.class))).thenAnswer(i -> i.getArguments()[0]);

        Page<CommentData> commentsPage = new PageImpl<>(List.of(new CommentData()));
        when(commentService.getPostComments(Mockito.any(Long.class), Mockito.any(), Mockito.any()))
                .thenReturn(commentsPage);
    }

    @Test
    void testCreatePost() {
        PostData post = new PostData();
        AssertHelper.assertException(CommonError.MISSING_ERROR, () -> postService.createPost(post));

        post.setUser(new UserData());
        Assertions.assertDoesNotThrow(() -> postService.createPost(post));
        assertThat(post.getStatistics()).isNotNull();
        Mockito.verify(postStatisticsService, times(1)).initialize(Mockito.any());
    }

    @Test
    void testModifyPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        UserData notOwner = new UserData();
        notOwner.setId(1L);
        PostData post = new PostData();
        post.setUser(notOwner);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postService.modifyPost(1L, 2L, post));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postService.modifyPost(2L, 2L, post));

        UserData owner = new UserData();
        owner.setId(2L);
        post.setUser(owner);
        when(postRepository.findById(3L)).thenReturn(Optional.of(post));

        final String changedContent = "changed";
        PostData postChange = new PostData();
        postChange.setContent(changedContent);

        Assertions.assertDoesNotThrow(() -> postService.modifyPost(3L, 2L, postChange));
        assertThat(postChange.getContent()).isEqualTo(changedContent);
        Mockito.verify(postRepository, times(1)).save(Mockito.any(PostData.class));
    }

    @Test
    void testRemovePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        UserData notOwner = new UserData();
        notOwner.setId(1L);
        PostData post = new PostData();
        post.setUser(notOwner);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postService.removePost(1L, 2L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> postService.removePost(2L, 2L));

        UserData owner = new UserData();
        owner.setId(2L);
        post.setId(3L);
        post.setUser(owner);
        when(postRepository.findById(3L)).thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.removePost(3L, 2L));
        Mockito.verify(postStatisticsService, times(1)).remove(3L);
        Mockito.verify(postRepository, times(1)).delete(Mockito.any(PostData.class));
    }

    @Test
    void testGetBrowsePosts() {
        UserData user = new UserData();
        user.setId(1L);
        PostData postOne = new PostData();
        postOne.setUser(user);
        PostData postTwo = new PostData();
        postTwo.setUser(user);
        List<PostData> list = Arrays.asList(postOne, postTwo);
        Page<PostData> page = new PageImpl<>(list);
        when(postRepository.findPostsForUser(Mockito.any(), Mockito.any())).thenReturn(page);

        Assertions.assertDoesNotThrow(() -> postService.getBrowsePosts(1L, null));
        assertThat(list).allMatch(p -> p.getStatistics() != null && p.getComments().size() == 1);
    }

    @Test
    void testGetUserPosts() {
        UserData user = new UserData();
        user.setId(1L);
        PostData postOne = new PostData();
        postOne.setUser(user);
        PostData postTwo = new PostData();
        postTwo.setUser(user);
        List<PostData> list = Arrays.asList(postOne, postTwo);
        Page<PostData> page = new PageImpl<>(list);
        when(postRepository.findByUserId(Mockito.any(), Mockito.any())).thenReturn(page);

        Assertions.assertDoesNotThrow(() -> postService.getUserPosts(1L, null));
        assertThat(list).allMatch(p -> p.getStatistics() != null && p.getComments().size() == 1);
    }
}
