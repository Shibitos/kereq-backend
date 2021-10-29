package com.kereq.main.repository;

import com.kereq.main.entity.RoleData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleData, Short> {

    RoleData findByCode(String code);
}
