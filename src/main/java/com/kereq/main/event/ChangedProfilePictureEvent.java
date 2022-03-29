package com.kereq.main.event;

import com.kereq.common.event.BaseEvent;

import java.time.Clock;

public class ChangedProfilePictureEvent extends BaseEvent {

    public ChangedProfilePictureEvent(Long userId) {
        super(userId);
    }

    public ChangedProfilePictureEvent(Long userId, Clock clock) {
        super(userId, clock);
    }
}
