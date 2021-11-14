package com.kereq.main.repository;

import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserData, Long> {

    boolean existsByEmailIgnoreCase(String email);

    UserData findByEmailIgnoreCase(String email);
}
