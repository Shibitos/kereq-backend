package com.kereq.main.repository;

import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserData, Long> {

    Boolean existsByEmail(String email);
    Boolean existsByLogin(String login);
    Optional<UserData> findByLogin(String login);
}
