package com.kereq.authorization.repository;

import com.kereq.authorization.entity.TokenData;
import com.kereq.common.repository.BaseRepository;

import java.util.Date;

public interface TokenRepository extends BaseRepository<TokenData> {

    boolean existsByUserIdAndType(Long userId, String tokenType);

    TokenData findByUserIdAndType(Long userId, String tokenType);

    TokenData findByValue(String value);

    void deleteByExpireDateLessThan(Date date);
}
