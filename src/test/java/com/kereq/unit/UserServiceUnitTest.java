package com.kereq.unit;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.FriendshipData;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.FriendshipRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.main.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testInviteFriend() {
        when(friendshipRepository.existsByUserIdAndFriendId(1L, 2L)).thenReturn(true);
        when(friendshipRepository.existsByUserIdAndFriendId(2L, 1L)).thenReturn(false);

        when(friendshipRepository.existsByUserIdAndFriendId(1L, 3L)).thenReturn(false);
        when(friendshipRepository.existsByUserIdAndFriendId(3L, 1L)).thenReturn(true);

        when(friendshipRepository.existsByUserIdAndFriendId(1L, 4L)).thenReturn(false);
        when(friendshipRepository.existsByUserIdAndFriendId(4L, 1L)).thenReturn(false);

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> userService.inviteFriend(1L, 2L));
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> userService.inviteFriend(2L, 1L));

        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> userService.inviteFriend(1L, 3L));
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> userService.inviteFriend(3L, 1L));

        ArgumentCaptor<FriendshipData> captor = ArgumentCaptor.forClass(FriendshipData.class);
        userService.inviteFriend(1L, 4L);
        Mockito.verify(friendshipRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(FriendshipData.FriendshipStatus.INVITED);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getFriendId()).isEqualTo(4L);

        userService.inviteFriend(4L, 1L);
        Mockito.verify(friendshipRepository, times(2)).save(Mockito.any(FriendshipData.class));
    }

    @Test
    void testRemoveInvitation() {
        when(friendshipRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);

        FriendshipData wrongStatus = new FriendshipData();
        wrongStatus.setStatus(FriendshipData.FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findByUserIdAndFriendId(1L, 3L)).thenReturn(wrongStatus);

        FriendshipData goodStatus = new FriendshipData();
        goodStatus.setStatus(FriendshipData.FriendshipStatus.INVITED);
        when(friendshipRepository.findByUserIdAndFriendId(1L, 4L)).thenReturn(goodStatus);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> userService.removeInvitation(1L, 2L));

        AssertHelper.assertException(CommonError.INVALID_ERROR,
                () -> userService.removeInvitation(1L, 3L));

        userService.removeInvitation(1L, 4L);
        Mockito.verify(friendshipRepository, times(1)).delete(Mockito.any(FriendshipData.class));
    }

    @Test
    void testAcceptInvitation() {
        when(friendshipRepository.findByUserIdAndFriendId(2L, 1L)).thenReturn(null);

        FriendshipData wrongStatus = new FriendshipData();
        wrongStatus.setStatus(FriendshipData.FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findByUserIdAndFriendId(3L, 1L)).thenReturn(wrongStatus);

        FriendshipData goodStatus = new FriendshipData();
        goodStatus.setStatus(FriendshipData.FriendshipStatus.INVITED);
        when(friendshipRepository.findByUserIdAndFriendId(4L, 1L)).thenReturn(goodStatus);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> userService.acceptInvitation(1L, 2L));

        AssertHelper.assertException(CommonError.INVALID_ERROR,
                () -> userService.acceptInvitation(1L, 3L));

        ArgumentCaptor<FriendshipData> captor = ArgumentCaptor.forClass(FriendshipData.class);
        userService.acceptInvitation(1L, 4L);
        Mockito.verify(friendshipRepository, times(2)).save(captor.capture());
        Assertions.assertThat(captor.getAllValues())
                .allMatch(f -> FriendshipData.FriendshipStatus.ACCEPTED.equals(f.getStatus()));
    }

    @Test
    void testRejectInvitation() {
        when(friendshipRepository.findByUserIdAndFriendId(2L, 1L)).thenReturn(null);

        FriendshipData wrongStatus = new FriendshipData();
        wrongStatus.setStatus(FriendshipData.FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findByUserIdAndFriendId(3L, 1L)).thenReturn(wrongStatus);

        FriendshipData goodStatus = new FriendshipData();
        goodStatus.setStatus(FriendshipData.FriendshipStatus.INVITED);
        when(friendshipRepository.findByUserIdAndFriendId(4L, 1L)).thenReturn(goodStatus);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> userService.rejectInvitation(1L, 2L));

        AssertHelper.assertException(CommonError.INVALID_ERROR,
                () -> userService.rejectInvitation(1L, 3L));

        userService.rejectInvitation(1L, 4L);
        Mockito.verify(friendshipRepository, times(1)).save(Mockito.any(FriendshipData.class));
        assertThat(goodStatus.getStatus()).isEqualTo(FriendshipData.FriendshipStatus.DECLINED);
    }


    @Test
    void testRemoveFriend() {
        when(friendshipRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(null);

        when(friendshipRepository.findByUserIdAndFriendId(1L, 3L)).thenReturn(new FriendshipData());
        when(friendshipRepository.findByUserIdAndFriendId(3L, 1L)).thenReturn(null);

        FriendshipData userEntry = new FriendshipData();
        when(friendshipRepository.findByUserIdAndFriendId(1L, 4L)).thenReturn(userEntry);
        FriendshipData friendEntry = new FriendshipData();
        when(friendshipRepository.findByUserIdAndFriendId(4L, 1L)).thenReturn(friendEntry);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> userService.removeFriend(1L, 2L));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> userService.removeFriend(1L, 3L));

        userService.removeFriend(1L, 4L);
        Mockito.verify(friendshipRepository, times(1)).delete(userEntry);
        Mockito.verify(friendshipRepository, times(1)).delete(friendEntry);
    }

    @Test
    void testGetUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new UserData()));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND, () -> userService.getUser(1L));

        userService.getUser(2L);
    }
}
