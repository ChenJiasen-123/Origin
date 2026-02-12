package com.origin.entity;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    // 使用 JPQL 实现多字段模糊匹配
    @Query("SELECT f FROM Friend f WHERE " +
            "f.name LIKE %:keyword% OR " +
            "f.hometown LIKE %:keyword% OR " +
            "f.tags LIKE %:keyword% OR " +
            "f.note LIKE %:keyword%")
    List<Friend> searchFriends(@Param("keyword") String keyword);
}
