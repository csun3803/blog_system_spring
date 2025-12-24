package com.example.blog_system_spring.controller;

import com.example.blog_system_spring.common.ApiResponse;
import com.example.blog_system_spring.entity.Blog;
import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.service.BlogService;
import com.example.blog_system_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/author")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthorController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    /**
     * 根据博客ID获取作者信息
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getAuthorInfo(@RequestParam Integer blogId) {
        if (blogId == null) {
            return ApiResponse.error("参数缺失!");
        }

        try {
            // 查找博客
            Blog blog = blogService.getBlogById(blogId);
            if (blog == null) {
                return ApiResponse.error("要查询的博客不存在!!");
            }

            // 查找作者
            User author = userService.selectById(blog.getUserId());
            if (author == null) {
                return ApiResponse.error("要查询的用户不存在!!");
            }

            // 清除敏感信息
            author.setPassword("");

            // 构建响应
            Map<String, Object> result = new HashMap<>();
            result.put("author", author);
            result.put("blogId", blogId);
            result.put("blogTitle", blog.getTitle());

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("服务器错误: " + e.getMessage());
        }
    }
}