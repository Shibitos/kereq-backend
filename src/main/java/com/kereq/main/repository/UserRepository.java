package com.kereq.main.repository;

import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserData, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLoginIgnoreCase(String login);

    UserData findByLogin(String login);

    @Query("SELECT u FROM UserData u WHERE u.login=?1 OR u.email=?1")
    UserData findByLoginOrEmail(String loginOrEmail);
}
