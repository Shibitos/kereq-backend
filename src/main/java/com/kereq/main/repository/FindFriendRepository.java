package com.kereq.main.repository;

import com.kereq.main.entity.FindFriendData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FindFriendRepository extends JpaRepository<FindFriendData, Long> {

    boolean existsByUserId(Long userId);

    FindFriendData findByUserId(Long userId);

    @Query(value = "SELECT ff FROM FindFriendData ff JOIN UserData u " +
            "WHERE u.id = :userId AND ff.minAge <= :age AND ff.maxAge >= :age" +
            " AND (ff.gender IS NULL OR ff.gender = u.gender)")
    Page<FindFriendData> findAdsForUserId(Long userId, int age, Pageable page);
}