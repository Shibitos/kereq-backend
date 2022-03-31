package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends BaseRepository<UserData> {

    boolean existsByEmailIgnoreCase(String email);

    UserData findByEmailIgnoreCase(String email);

    @Modifying
    @Query("UPDATE UserData user SET user.online = :online WHERE user.id = :id")
    void setOnlineByUserId(@Param("id") long id, @Param("online") boolean online);
}
