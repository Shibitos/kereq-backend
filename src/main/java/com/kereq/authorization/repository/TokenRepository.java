package com.kereq.authorization.repository;

import com.kereq.authorization.entity.TokenData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface TokenRepository extends JpaRepository<TokenData, Long> {

    boolean existsByUserIdAndType(Long userId, String tokenType);

    TokenData findByValue(String value);

    void deleteByExpireDateLessThan(Date date);
}
