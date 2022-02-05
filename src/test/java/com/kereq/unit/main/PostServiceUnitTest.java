package com.kereq.unit.main;

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
import com.kereq.main.service.UserService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class PostServiceUnitTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostStatisticsService postStatisticsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(postStatisticsService.initialize(Mockito.anyLong())).thenReturn(new PostStatisticsData());
        when(postStatisticsService.getStatistics(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new PostStatisticsData());
        when(postRepository.save(Mockito.any(PostData.class))).thenAnswer(i -> {
            PostData post = (PostData) i.getArguments()[0];
            post.setId(1L);
            return post;
        });

        Page<CommentData> commentsPage = new PageImpl<>(List.of(new CommentData()));
        when(commentService.getPostComments(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(commentsPage);
    }

    @Test
    void testCreatePost() {
        PostData post = new PostData();
        AssertHelper.assertException(CommonError.MISSING_ERROR, () -> postService.createPost(post));

        post.setUser(new UserData());
        postService.createPost(post);
        assertThat(post.getStatistics()).isNotNull();
        Mockito.verify(postStatisticsService, times(1)).initialize(Mockito.anyLong());
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

        postService.modifyPost(3L, 2L, postChange);
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

        postService.removePost(3L, 2L);
        Mockito.verify(postStatisticsService, times(1)).remove(3L);
        Mockito.verify(postRepository, times(1)).delete(Mockito.any(PostData.class));
    }

    @Test
    void testGetBrowsePosts() {
        UserData user = new UserData();
        user.setId(1L);
        PostData postOne = new PostData();
        postOne.setId(1L);
        postOne.setUser(user);
        PostData postTwo = new PostData();
        postTwo.setId(2L);
        postTwo.setUser(user);
        List<PostData> list = Arrays.asList(postOne, postTwo);
        Page<PostData> page = new PageImpl<>(list);
        when(postRepository.findPostsForUser(Mockito.anyLong(), Mockito.any())).thenReturn(page);

        postService.getBrowsePosts(1L, null);
        assertThat(list).allMatch(p -> p.getStatistics() != null && p.getComments().size() == 1);
        Mockito.verify(userService, times(list.size())).loadProfilePhoto(Mockito.any(UserData.class));
    }

    @Test
    void testGetUserPosts() {
        UserData user = new UserData();
        user.setId(1L);
        PostData postOne = new PostData();
        postOne.setId(1L);
        postOne.setUser(user);
        PostData postTwo = new PostData();
        postTwo.setId(2L);
        postTwo.setUser(user);
        List<PostData> list = Arrays.asList(postOne, postTwo);
        Page<PostData> page = new PageImpl<>(list);
        when(postRepository.findByUserId(Mockito.anyLong(), Mockito.any())).thenReturn(page);

        postService.getUserPosts(1L, null);
        assertThat(list).allMatch(p -> p.getStatistics() != null && p.getComments().size() == 1);
        Mockito.verify(userService, times(list.size())).loadProfilePhoto(Mockito.any(UserData.class));
    }
}
