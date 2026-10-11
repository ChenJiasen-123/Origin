package com.origin.entity;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 使用 JPQL 实现多字段模糊匹配
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND (" +
            "f.name LIKE %:keyword% OR " +
            "f.hometown LIKE %:keyword% OR " +
            "f.tags LIKE %:keyword% OR " +
            "f.note LIKE %:keyword%)")
    List<Friend> searchFriends(@Param("userId") Long userId, @Param("keyword") String keyword);

    List<Friend> findAllByUserId(Long userId);

    Optional<Friend> findByIdAndUserId(Long id, Long userId);
}
