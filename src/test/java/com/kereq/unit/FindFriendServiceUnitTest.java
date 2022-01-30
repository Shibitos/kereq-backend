package com.kereq.unit;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.util.DateUtil;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.FindFriendRepository;
import com.kereq.main.service.FindFriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class FindFriendServiceUnitTest {

    @Mock
    private FindFriendRepository findFriendRepository;

    @InjectMocks
    private FindFriendService findFriendService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(findFriendRepository.save(Mockito.any(FindFriendData.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void testCreateFindFriendAd() {
        when(findFriendRepository.existsByUserId(1L)).thenReturn(true);
        when(findFriendRepository.existsByUserId(2L)).thenReturn(false);
        FindFriendData findFriendAd = new FindFriendData();
        AssertHelper.assertException(CommonError.MISSING_ERROR,
                () -> findFriendService.createFindFriendAd(findFriendAd));

        UserData user = new UserData();
        user.setId(1L);
        findFriendAd.setUser(user);
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> findFriendService.createFindFriendAd(findFriendAd));

        user.setId(2L);
        findFriendService.createFindFriendAd(findFriendAd);
        Mockito.verify(findFriendRepository, times(1)).save(Mockito.any(FindFriendData.class));
    }

    @Test
    void testModifyFindFriendAd() {
        when(findFriendRepository.findByUserId(1L)).thenReturn(null);
        FindFriendData original = new FindFriendData();
        when(findFriendRepository.findByUserId(2L)).thenReturn(original);

        FindFriendData changed = new FindFriendData();
        changed.setGender("G");
        changed.setMaxAge(5);
        changed.setMinAge(1);
        changed.setDescription("descriptChanged");
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> findFriendService.modifyFindFriendAd(1L, changed));

        findFriendService.modifyFindFriendAd(2L, changed);
        assertThat(original.getGender()).isEqualTo(changed.getGender());
        assertThat(original.getMaxAge()).isEqualTo(changed.getMaxAge());
        assertThat(original.getMinAge()).isEqualTo(changed.getMinAge());
        assertThat(original.getDescription()).isEqualTo(changed.getDescription());
        Mockito.verify(findFriendRepository, times(1)).save(Mockito.any(FindFriendData.class));
    }

    @Test
    void testRemoveFindFriendAd() {
        when(findFriendRepository.findByUserId(1L)).thenReturn(null);
        FindFriendData original = new FindFriendData();
        when(findFriendRepository.findByUserId(2L)).thenReturn(original);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> findFriendService.removeFindFriendAd(1L));

        findFriendService.removeFindFriendAd(2L);
        Mockito.verify(findFriendRepository, times(1)).delete(Mockito.any(FindFriendData.class));
    }

    @Test
    void testGetFindFriendAdByUserId() {
        when(findFriendRepository.findByUserId(1L)).thenReturn(null);
        FindFriendData original = new FindFriendData();
        original.setId(1L);
        when(findFriendRepository.findByUserId(2L)).thenReturn(original);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> findFriendService.getFindFriendAdByUserId(1L));

        FindFriendData returned = findFriendService.getFindFriendAdByUserId(2L);
        assertThat(returned.getId()).isEqualTo(1L);
    }

    @Test
    void testGetFindFriendAdsForUser() {
        when(findFriendRepository.findAdsForUserId(Mockito.any(Long.class), Mockito.eq(1), Mockito.any()))
                .thenReturn(null);
        Page<FindFriendData> page = new PageImpl<>(List.of(new FindFriendData()));
        when(findFriendRepository.findAdsForUserId(Mockito.any(Long.class), Mockito.eq(2), Mockito.any())).thenReturn(page);
        UserData user = new UserData();
        user.setId(1L);
        user.setBirthDate(DateUtil.addDays(DateUtil.now(), -367));

        Page<FindFriendData> returned = findFriendService.getFindFriendAdsForUser(user, null);
        assertThat(returned).isNull();

        user.setBirthDate(DateUtil.addDays(DateUtil.now(), -367 * 2));
        returned = findFriendService.getFindFriendAdsForUser(user, null);
        assertThat(returned.getTotalElements()).isEqualTo(1);
    }
}
