package com.kereq.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseDTO {

    public static final MapperVariant HIDE_ID = MapperVariant.builder().hide("id").build();
    public static final MapperVariant HIDE_AUDIT = MapperVariant.builder().hide("auditCD").hide("auditCU")
            .hide("auditMD").hide("auditMU").hide("auditRD").build();

    public BaseDTO applyVariant(MapperVariant variant) {
        variant.apply(this);
        return this;
    }
}
