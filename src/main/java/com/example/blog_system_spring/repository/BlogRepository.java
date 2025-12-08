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

    // 1. 插入博客 (对应原insert方法)
    // Spring Data JPA会自动提供save()方法，无需额外定义

    // 2. 获取所有博客，按时间倒序 (对应原selectAll方法)
    // 注意：原方法有内容截取，这里我们先获取完整内容，截取逻辑放在Service层
    List<Blog> findAllByOrderByPostTimeDesc();

    // 3. 根据博客id获取指定博客 (对应原selectOne方法)
    // JPA会自动提供findById()方法，这里我们明确声明一个
    Optional<Blog> findByBlogId(Integer blogId);

    // 4. 根据博客id删除博客 (对应原delete方法)
    // JPA会自动提供deleteById()方法
    @Modifying
    @Transactional
    @Query("DELETE FROM Blog b WHERE b.blogId = :blogId")
    int deleteByBlogId(@Param("blogId") Integer blogId);

    // 5. 修改博客 (对应原update方法)
    // JPA的save()方法既可以插入也可以更新

    // 6. 计算个人文章总数 (对应原selectTotal方法)
    @Query("SELECT COUNT(b) FROM Blog b WHERE b.userId = :userId")
    Integer countByUserId(@Param("userId") Integer userId);

    // ========== 额外添加的常用查询方法 ==========

    // 根据用户ID查找所有博客（按时间倒序）
    List<Blog> findByUserIdOrderByPostTimeDesc(Integer userId);

    // 根据用户ID查找博客（分页）
    Page<Blog> findByUserId(Integer userId, Pageable pageable);

    // 根据标题关键词搜索博客
    @Query("SELECT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY b.postTime DESC")
    List<Blog> findByTitleContainingIgnoreCase(@Param("keyword") String keyword);

    // 查找最新发布的博客（限制数量）
    List<Blog> findTop10ByOrderByPostTimeDesc();

    // 统计总博客数
    @Query("SELECT COUNT(b) FROM Blog b")
    Integer countAllBlogs();
}