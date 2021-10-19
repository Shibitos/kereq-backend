package com.kereq.main.repository;

import com.kereq.main.entity.RoleData;
import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleData, Short> {

    RoleData findByCode(String code);
}
