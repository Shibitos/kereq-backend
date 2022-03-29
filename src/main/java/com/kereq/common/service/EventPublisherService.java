package com.kereq.common.service;

import com.kereq.common.event.BaseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisherService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(BaseEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
