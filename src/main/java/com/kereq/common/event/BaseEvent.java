package com.kereq.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public abstract class BaseEvent extends ApplicationEvent {

    private final Long userId;

    protected BaseEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    protected BaseEvent(Long userId, Clock clock) {
        super(userId, clock);
        this.userId = userId;
    }
}
