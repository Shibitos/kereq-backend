package com.kereq.main.repository;

import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserData, Long> {

    boolean existsByEmailIgnoreCase(String email);

    UserData findByEmailIgnoreCase(String email);
}
