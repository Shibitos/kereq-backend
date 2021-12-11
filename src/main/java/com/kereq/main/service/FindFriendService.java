package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.UserData;
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

    public void createFindFriendAd(FindFriendData findFriendData) { //TODO: sanitize html
        if (findFriendData.getUser() == null) {
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        if (findFriendRepository.existsByUserId(findFriendData.getUser().getId())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        findFriendRepository.save(findFriendData);
    }

    public void modifyFindFriendAd(FindFriendData findFriendData) {
        FindFriendData original = findFriendRepository
                .findByUserId(findFriendData.getUser().getId());
        if (original == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
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
