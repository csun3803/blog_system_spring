package com.example.blog_system_spring.service;

import com.example.blog_system_spring.entity.Blog;
import com.example.blog_system_spring.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;


    public List<Blog> getAllBlogsForList() {
        // 这里直接使用 JPA 的方法，它会自动使用正确的列名
        return blogRepository.findAllByOrderByPostTimeDesc();
    }

    public Blog getBlogById(Integer blogId) {
        if (blogId == null) {
            return null;
        }
        Optional<Blog> blog = blogRepository.findByBlogId(blogId);
        return blog.orElse(null);
    }

    @Transactional
    public Blog createBlog(String title, String content, Integer userId) {
        // 参数验证（与原Servlet一致）
        if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty() ||
                userId == null) {
            throw new IllegalArgumentException("提交博客失败! 缺少必要的参数!");
        }

        Blog blog = new Blog();
        blog.setTitle(title.trim());
        blog.setContent(content.trim());
        blog.setUserId(userId);
        blog.setPostTime(LocalDateTime.now());

        return blogRepository.save(blog);
    }

    @Transactional
    public boolean deleteBlog(Integer blogId, Integer userId) {
        // 参数验证
        if (blogId == null) {
            throw new IllegalArgumentException("当前blogId参数不对!");
        }

        // 获取要删除的博客
        Blog blog = getBlogById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("当前要删除的博客不存在!");
        }

        // 权限检查：当前登录用户是否为博客的作者
        if (!blog.getUserId().equals(userId)) {
            throw new SecurityException("当前登录的用户不是作者，没有权限删除!");
        }

        // 执行删除
        try {
            blogRepository.delete(blog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Blog getBlogForEdit(Integer blogId, Integer userId) {
        if (blogId == null) {
            throw new IllegalArgumentException("当前blogId参数不对!");
        }

        Blog blog = getBlogById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("当前要修改的博客不存在!");
        }

        // 权限检查
        if (!blog.getUserId().equals(userId)) {
            throw new SecurityException("没有权限编辑此博客!");
        }

        return blog;
    }

    @Transactional
    public Blog updateBlog(Integer blogId, String title, String content, Integer userId) {
        // 参数验证
        if (blogId == null) {
            throw new IllegalArgumentException("博客ID不能为空!");
        }
        if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("标题或内容不能为空!");
        }

        // 获取要更新的博客（带权限检查）
        Blog existingBlog = getBlogForEdit(blogId, userId);

        // 更新字段
        existingBlog.setTitle(title.trim());
        existingBlog.setContent(content.trim());

        // 保存更新
        return blogRepository.save(existingBlog);
    }

    private List<Blog> processBlogsForList(List<Blog> blogs) {
        List<Blog> result = new ArrayList<>();

        for (Blog blog : blogs) {
            // 创建新对象或使用副本（避免修改原始实体）
            Blog listBlog = new Blog();
            listBlog.setBlogId(blog.getBlogId());
            listBlog.setTitle(blog.getTitle());
            listBlog.setUserId(blog.getUserId());
            listBlog.setPostTime(blog.getPostTime());

            // 内容截取逻辑（与原Dao完全一致）
            String content = blog.getContent();
            if (content != null && content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }
            listBlog.setContent(content);

            result.add(listBlog);
        }
        return result;
    }

    public Integer getUserBlogCount(Integer userId) {
        return blogRepository.countByUserId(userId);
    }

    public List<Blog> getBlogsByUser(Integer userId) {
        return blogRepository.findByUserIdOrderByPostTimeDesc(userId);
    }
}