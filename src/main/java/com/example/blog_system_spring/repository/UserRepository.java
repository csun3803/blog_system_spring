package com.example.blog_system_spring.repository;

import com.example.blog_system_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(Integer userId);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.userId = :identifier")
    Optional<User> findByUsernameOrUserId(@Param("identifier") String identifier);

    long count();
}