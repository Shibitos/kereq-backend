package com.kereq.main.repository;

import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserData, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLoginIgnoreCase(String login);

    UserData findByLoginIgnoreCase(String login);

    UserData findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM UserData u WHERE UPPER(u.login)=UPPER(?1) OR UPPER(u.email)=UPPER(?1)")
    UserData findByLoginOrEmail(String loginOrEmail);
}
