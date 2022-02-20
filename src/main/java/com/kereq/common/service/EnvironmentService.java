package com.kereq.common.service;

import com.kereq.common.constant.CacheName;
import com.kereq.common.error.CommonError;
import com.kereq.main.exception.ApplicationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService implements EnvironmentAware {

    private Environment environment;

    @Cacheable(value = CacheName.PARAMS, key = "#key")
    public String getParam(String key) {
        String value = environment.getProperty(key);
        if (value == null) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        return value;
    }

    public Integer getParamInteger(String key) {
        return Integer.valueOf(getParam(key));
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
