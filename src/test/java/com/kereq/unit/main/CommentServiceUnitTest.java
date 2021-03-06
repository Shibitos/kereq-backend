package com.kereq.unit.main;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.CommentData;
import com.kereq.main.entity.CommentStatisticsData;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.CommentRepository;
import com.kereq.main.service.CommentService;
import com.kereq.main.service.CommentStatisticsService;
import com.kereq.main.service.PostStatisticsService;
import com.kereq.main.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommentServiceUnitTest {

    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    private final PostStatisticsService postStatisticsService = Mockito.mock(PostStatisticsService.class);

    private final CommentStatisticsService commentStatisticsService = Mockito.mock(CommentStatisticsService.class);

    private final UserService userService = Mockito.mock(UserService.class);

    private CommentService commentService;

    @BeforeEach
    public void setup() {
        when(commentStatisticsService.initialize()).thenReturn(new CommentStatisticsData());
        when(commentRepository.save(Mockito.any(CommentData.class))).thenAnswer(i -> {
            CommentData comment = (CommentData) i.getArguments()[0];
            comment.setId(1L);
            return comment;
        });
        commentService = new CommentService(commentRepository, userService, postStatisticsService, commentStatisticsService);
    }

    @Test
    void testCreateComment() {
        CommentData comment = new CommentData();
        AssertHelper.assertException(CommonError.MISSING_ERROR, () -> commentService.createComment(comment));

        comment.setUser(new UserData());
        comment.setPostId(1L);
        commentService.createComment(comment);
        assertThat(comment.getStatistics()).isNotNull();
        Mockito.verify(postStatisticsService, times(1)).addComment(1L);
    }

    @Test
    void testModifyComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        UserData notOwner = new UserData();
        notOwner.setId(1L);
        CommentData comment = new CommentData();
        comment.setUser(notOwner);
        comment.setPostId(1L);
        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentService.modifyComment(1L, 2L, comment));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentService.modifyComment(2L, 2L, comment));

        UserData owner = new UserData();
        owner.setId(2L);
        comment.setUser(owner);
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        final String changedContent = "changed";
        CommentData commentChange = new CommentData();
        commentChange.setContent(changedContent);

        commentService.modifyComment(3L, 2L, commentChange);
        assertThat(commentChange.getContent()).isEqualTo(changedContent);
        Mockito.verify(commentRepository, times(1)).save(Mockito.any(CommentData.class));
    }

    @Test
    void testRemoveComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        UserData notOwner = new UserData();
        notOwner.setId(1L);
        CommentData comment = new CommentData();
        comment.setUser(notOwner);
        comment.setPostId(1L);
        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentService.removeComment(1L, 2L));
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> commentService.removeComment(2L, 2L));

        UserData owner = new UserData();
        owner.setId(2L);
        comment.setUser(owner);
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        commentService.removeComment(3L, 2L);
        Mockito.verify(postStatisticsService, times(1)).removeComment(comment.getPostId());
        Mockito.verify(commentRepository, times(1)).delete(Mockito.any(CommentData.class));
    }

    @Test
    void testGetPostComments() {
        UserData user = new UserData();
        user.setId(1L);
        CommentData commentOne = new CommentData();
        commentOne.setId(1L);
        commentOne.setUser(user);
        commentOne.setStatistics(new CommentStatisticsData());
        CommentData commentTwo = new CommentData();
        commentTwo.setId(2L);
        commentTwo.setUser(user);
        commentTwo.setStatistics(new CommentStatisticsData());
        List<CommentData> list = Arrays.asList(commentOne, commentTwo);
        Page<CommentData> page = new PageImpl<>(list);
        when(commentRepository.findByPostId(Mockito.anyLong(), Mockito.any())).thenReturn(page);

        commentService.getPostComments(1L, 1L, null);
        verify(commentStatisticsService, times(list.size()))
                .fillUserLikeType(Mockito.anyLong(), Mockito.any(CommentStatisticsData.class));
    }
}
