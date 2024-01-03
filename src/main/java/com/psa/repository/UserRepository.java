package com.psa.repository;

import java.util.Set;

import com.psa.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User getUserByUsername(@Param("username") String username);

    @Query(nativeQuery = true, value ="SELECT sub.vessel_id FROM user INNER JOIN subscription sub ON sub.user_id = user.user_id WHERE user.username = :username")
    public Set<Integer> findAllSubscriptionByUsername(@Param("username") String username);

    public boolean existsByUsername(String username);
}
