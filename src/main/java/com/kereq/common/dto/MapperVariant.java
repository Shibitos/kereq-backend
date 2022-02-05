package com.kereq.common.dto;

import com.kereq.common.error.CommonError;
import com.kereq.main.exception.ApplicationException;
import lombok.Builder;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Builder
public class MapperVariant {

    private Set<String> hiddenFields;

    public static class MapperVariantBuilder {

        public MapperVariantBuilder hide(String fieldName) {
            if (this.hiddenFields == null) {
                this.hiddenFields = new HashSet<>();
            }
            this.hiddenFields.add(fieldName);
            return this;
        }
    }

    public void apply(BaseDTO object) {
        for (String fieldName : hiddenFields) {
            try {
                Field field = object.getClass().getDeclaredField(fieldName);
                object.getClass().getDeclaredMethod("set" + StringUtils.capitalize(field.getName()), field.getType()).invoke(object, (Object) null); //TODO: maybe another apply for DTO->entity? if hidden specified in request then throw
            } catch (Exception e) {
                throw new ApplicationException(CommonError.OTHER_ERROR);
            }
        }
    }
}
