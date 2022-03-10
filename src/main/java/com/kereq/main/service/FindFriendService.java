package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.util.DateUtil;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.FindFriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FindFriendService {

    private final FindFriendRepository findFriendRepository;

    public FindFriendService(FindFriendRepository findFriendRepository) {
        this.findFriendRepository = findFriendRepository;
    }

    public void createFindFriendAd(FindFriendData findFriendData) { //TODO: sanitize html
        if (findFriendData.getUser() == null) {
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        if (findFriendRepository.existsByUserId(findFriendData.getUser().getId())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        findFriendRepository.save(findFriendData);
    }

    public void modifyFindFriendAd(long userId, FindFriendData findFriendData) { //TODO: sanitize html
        FindFriendData original = findFriendRepository.findByUserId(userId);
        if (original == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        original.setMinAge(findFriendData.getMinAge());
        original.setMaxAge(findFriendData.getMaxAge());
        original.setGender(findFriendData.getGender());
        original.setDescription(findFriendData.getDescription());
        findFriendRepository.save(original);
    }

    public void removeFindFriendAd(long userId) {
        FindFriendData findFriendData = getFindFriendAdByUserId(userId);
        findFriendRepository.delete(findFriendData);
    }

    public FindFriendData getFindFriendAdByUserId(long userId) {
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
