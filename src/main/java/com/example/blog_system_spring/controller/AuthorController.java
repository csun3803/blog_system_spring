package com.example.blog_system_spring.controller;

import com.example.blog_system_spring.entity.Blog;
import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.service.BlogService;
import com.example.blog_system_spring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/info")
    public Map<String, Object> getAuthorInfo(@RequestParam(value = "blogId") Integer blogId) {
        Map<String, Object> response = new HashMap<>();

        if (blogId == null) {
            response.put("ok", false);
            response.put("reason", "参数缺失!");
            return response;
        }

        try {
            Blog blog = blogService.getBlogById(blogId);
            if (blog == null) {
                response.put("ok", false);
                response.put("reason", "要查询的博客不存在!!");
                return response;
            }

            User author = userService.selectById(blog.getUserId());
            if (author == null) {
                response.put("ok", false);
                response.put("reason", "要查询的用户不存在!!");
                return response;
            }

            author.setPassword("");

            response.put("ok", true);
            response.put("author", author);
            response.put("blogId", blogId);
            response.put("blogTitle", blog.getTitle());

        } catch (Exception e) {
            response.put("ok", false);
            response.put("reason", "服务器错误: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/infoWithStats")
    public Map<String, Object> getAuthorInfoWithStats(@RequestParam Integer blogId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> authorInfo = getAuthorInfo(blogId);
            if (!Boolean.TRUE.equals(authorInfo.get("ok"))) {
                return authorInfo;
            }

            User author = (User) authorInfo.get("author");
            Blog blog = blogService.getBlogById(blogId);

            Integer blogCount = blogService.getUserBlogCount(author.getUserId());

            response.putAll(authorInfo);
            response.put("blogCount", blogCount);
            response.put("blogCreatedTime", blog.getFormattedPostTime());

        } catch (Exception e) {
            response.put("ok", false);
            response.put("reason", e.getMessage());
        }

        return response;
    }
}