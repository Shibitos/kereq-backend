package com.kereq.main.service;

import com.kereq.authorization.error.AuthError;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.UserData;
import com.kereq.main.error.CommonError;
import com.kereq.main.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.FindFriendRepository;
import com.kereq.main.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FindFriendService {

    @Autowired
    private FindFriendRepository findFriendRepository;

    public void createFindFriendAd(FindFriendData findFriendData) {
        if (findFriendData.getUser() == null) {
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        if (findFriendRepository.existsByUserId(findFriendData.getUser().getId())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        findFriendRepository.save(findFriendData);
    }

    public void updateFindFriendAd(Long id, Long userId, FindFriendData findFriendData) {
        FindFriendData original = findFriendRepository
                .findById(id).orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!userId.equals(original.getUser().getId())) {
            throw new ApplicationException(AuthError.NO_ACCESS);
        }
        original.setMinAge(findFriendData.getMinAge());
        original.setMaxAge(findFriendData.getMaxAge());
        original.setGender(findFriendData.getGender());
        findFriendRepository.save(original);
    }

    public void removeFindFriendAd(Long userId) {
        FindFriendData findFriendData = getFindFriendAdByUserId(userId);
        findFriendRepository.delete(findFriendData);
    }

    public FindFriendData getFindFriendAdByUserId(Long userId) {
        FindFriendData findFriendData = findFriendRepository.findByUserId(userId);
        if (findFriendData == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        return findFriendData;
    }

    public Page<FindFriendData> getFindFriendAdsForUser(UserData user, Pageable page) {
        int userAge = DateUtil.yearsBetween(user.getBirthDate(), DateUtil.now());
        return findFriendRepository.findAdsForUserId(user.getId(), userAge, page);
    }
}
