package com.example.blog_system_spring.controller;

import com.example.blog_system_spring.common.ApiResponse;
import com.example.blog_system_spring.entity.Blog;
import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 获取博客列表（根据登录状态自动决定）
     * 1. 如果用户已登录：只返回该用户的博客
     * 2. 如果用户未登录：返回所有博客
     */
    @GetMapping("/list")
    public ApiResponse<List<Blog>> getBlogs(HttpSession session) {
        User user = (User) session.getAttribute("user");

        List<Blog> blogs;

        if (user != null) {
            // 用户已登录，只返回该用户的博客
            blogs = blogService.getBlogsByUser(user.getUserId());
        } else {
            // 用户未登录，返回所有博客
            blogs = blogService.getAllBlogs();
        }

        return ApiResponse.success(blogs);
    }

    /**
     * 获取所有博客（公共接口，无论是否登录都返回所有）
     * 用于特定页面需要查看所有博客的情况
     */
    @GetMapping("/all")
    public ApiResponse<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        return ApiResponse.success(blogs);
    }

    /**
     * 获取单个博客详情
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<Blog> getBlogDetail(@PathVariable Integer id) {
        Blog blog = blogService.getBlogById(id);
        if (blog == null) {
            return ApiResponse.error("博客不存在");
        }
        return ApiResponse.success(blog);
    }

    /**
     * 创建博客
     */
    @PostMapping("/create")
    public ApiResponse<Blog> createBlog(
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session) {

        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error("当前用户未登录，不能提交博客!");
        }

        try {
            Blog blog = blogService.createBlog(title, content, user.getUserId());
            return ApiResponse.success("博客创建成功", blog);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新博客
     */
    @PostMapping("/update/{id}")
    public ApiResponse<Blog> updateBlog(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session) {

        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error("当前尚未登录，不能修改!");
        }

        try {
            Blog blog = blogService.updateBlog(id, title, content, user.getUserId());
            return ApiResponse.success("博客更新成功", blog);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除博客
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteBlog(
            @PathVariable Integer id,
            HttpSession session) {

        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error("当前尚未登录，不能删除!");
        }

        try {
            boolean success = blogService.deleteBlog(id, user.getUserId());
            if (success) {
                return ApiResponse.success("博客删除成功", null);
            } else {
                return ApiResponse.error("博客删除失败!");
            }
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户的博客列表
     */
    @GetMapping("/my-blogs")
    public ApiResponse<List<Blog>> getMyBlogs(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error("请先登录");
        }

        List<Blog> blogs = blogService.getBlogsByUser(user.getUserId());
        return ApiResponse.success(blogs);
    }

    /**
     * 获取用户的博客数量
     */
    @GetMapping("/count/{userId}")
    public ApiResponse<Integer> getUserBlogCount(@PathVariable Integer userId) {
        Integer count = blogService.getUserBlogCount(userId);
        return ApiResponse.success(count);
    }
}