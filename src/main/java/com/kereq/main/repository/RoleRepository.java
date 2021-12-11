package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.RoleData;

public interface RoleRepository extends BaseRepository<RoleData> {

    RoleData findByCode(String code);
}
