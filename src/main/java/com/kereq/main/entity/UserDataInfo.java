package com.kereq.main.entity;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Set;

public interface UserDataInfo extends UserDetails {
    Long getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    boolean isActivated();

    String getCountry();

    Date getBirthDate();

    Date getAuditCD();

    Set<RoleData> getRoles();
}
