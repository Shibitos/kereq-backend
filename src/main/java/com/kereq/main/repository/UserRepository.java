package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.UserData;

public interface UserRepository extends BaseRepository<UserData> {

    boolean existsByEmailIgnoreCase(String email);

    UserData findByEmailIgnoreCase(String email);
}
