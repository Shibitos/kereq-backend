package com.kereq.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ParamRange {

    IMG_DIR_DIV_CHAR_COUNT(ParamKey.IMG_DIR_DIV_CHAR_COUNT, 1, 32),
    IMG_DIR_DIV_MAX_LEVEL(ParamKey.IMG_DIR_DIV_MAX_LEVEL, 1, 0),
    IMG_MIN_SIZE(ParamKey.IMG_MIN_SIZE, 50, 0),
    PHOTO_MAX_WIDTH(ParamKey.PHOTO_MAX_WIDTH, 50, 5000),
    PHOTO_MAX_HEIGHT(ParamKey.PHOTO_MAX_HEIGHT, 50, 5000),
    PHOTO_PROFILE_MAX_WIDTH(ParamKey.PHOTO_PROFILE_MAX_WIDTH, 50, 5000),
    PHOTO_PROFILE_MAX_HEIGHT(ParamKey.PHOTO_PROFILE_MAX_HEIGHT, 50, 5000);

    String key;
    double min;
    double max;
}
