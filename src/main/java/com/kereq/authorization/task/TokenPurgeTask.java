package com.kereq.authorization.task;

import com.kereq.authorization.repository.TokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
@Transactional
public class TokenPurgeTask { //TODO: tasks system

    private final TokenRepository tokenRepository;

    public TokenPurgeTask(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "${purge.cron.expression}") //TODO: move to parameters?
    public void purgeExpired() {
        Date now = Date.from(Instant.now());
        tokenRepository.deleteByExpireDateLessThan(now);
    }
}
