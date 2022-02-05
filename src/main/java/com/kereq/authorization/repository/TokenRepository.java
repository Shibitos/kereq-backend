package com.kereq.authorization.repository;

import com.kereq.authorization.entity.TokenData;
import com.kereq.common.repository.BaseRepository;

import java.util.Date;
import java.util.UUID;

public interface TokenRepository extends BaseRepository<TokenData> {

    boolean existsByUserIdAndType(long userId, String tokenType);

    TokenData findByUserIdAndType(long userId, String tokenType);

    TokenData findByValue(UUID value);

    void deleteByExpireDateLessThan(Date date);
}
