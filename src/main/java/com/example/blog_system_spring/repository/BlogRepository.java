package com.example.blog_system_spring.repository;

import com.example.blog_system_spring.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    List<Blog> findAllByOrderByPostTimeDesc();

    Optional<Blog> findByBlogId(Integer blogId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Blog b WHERE b.blogId = :blogId")
    int deleteByBlogId(@Param("blogId") Integer blogId);

    @Query("SELECT COUNT(b) FROM Blog b WHERE b.userId = :userId")
    Integer countByUserId(@Param("userId") Integer userId);

    List<Blog> findByUserIdOrderByPostTimeDesc(Integer userId);

    Page<Blog> findByUserId(Integer userId, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY b.postTime DESC")
    List<Blog> findByTitleContainingIgnoreCase(@Param("keyword") String keyword);

    List<Blog> findTop10ByOrderByPostTimeDesc();

    @Query("SELECT COUNT(b) FROM Blog b")
    Integer countAllBlogs();
}