package com.kereq.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QueueName {

    MESSAGES(Constant.MESSAGES);

    String name;

    public interface Constant {

        String MESSAGES = "messages";
    }
}
