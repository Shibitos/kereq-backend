package com.kereq.common.service;

import com.kereq.common.constant.CacheName;
import com.kereq.common.constant.ParamKey;
import com.kereq.common.constant.ParamRange;
import com.kereq.common.error.CommonError;
import com.kereq.main.exception.ApplicationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EnvironmentService implements EnvironmentAware {

    private Environment environment;

    @Cacheable(value = CacheName.PARAMS, key = "#key")
    public String getParam(String key) {
        return getEnvProperty(key);
    }

    @Cacheable(value = CacheName.PARAMS, key = "#key")
    public Integer getParamInteger(String key) {
        int value = Integer.parseInt(getEnvProperty(key));
        boolean rangeConditionFailed = Arrays.stream(ParamRange.values())
                .anyMatch(pr -> pr.getKey().equals(key)
                        && ((pr.getMin() > 0 && value < pr.getMin()) || (pr.getMax() > 0 && value > pr.getMax())));
        if (rangeConditionFailed) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        return value;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    private String getEnvProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        return value;
    }
}
