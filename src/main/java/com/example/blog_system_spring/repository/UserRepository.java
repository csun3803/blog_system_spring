package com.example.blog_system_spring.repository;

import com.example.blog_system_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // ========== 对应原UserDao的方法 ==========

    /**
     * 1. 插入用户
     * 对应原insert()方法
     * 注意：JPA的save()方法既可用于插入也可用于更新
     */
    // save()方法已由JpaRepository提供，无需额外定义

    /**
     * 2. 根据用户名查找用户信息（登录逻辑中使用）
     * 对应原selectByName()方法
     */
    Optional<User> findByUsername(String username);

    /**
     * 3. 根据用户ID查找用户信息（博客详情页显示作者名）
     * 对应原selectById()方法
     */
    // findById()方法已由JpaRepository提供，但这里明确声明一个更符合原项目命名
    Optional<User> findByUserId(Integer userId);

    // ========== 额外添加的实用方法 ==========

    /**
     * 检查用户名是否存在
     * 用于注册时的用户名唯一性验证
     */
    boolean existsByUsername(String username);

    /**
     * 通过用户名或ID查找用户
     * 更灵活的查询方法
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.userId = :identifier")
    Optional<User> findByUsernameOrUserId(@Param("identifier") String identifier);

    /**
     * 统计用户总数
     */
    long count();
}