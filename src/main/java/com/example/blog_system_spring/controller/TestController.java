package com.example.blog_system_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    /**
     * 测试页面，验证模板引擎
     */
    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "模板引擎工作正常！");
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "test";
    }

    /**
     * 直接测试登录页面
     */
    @GetMapping("/test-login")
    public String testLogin() {
        return "auth/blog_login";  // 直接返回模板路径
    }

    /**
     * 直接测试博客列表
     */
    @GetMapping("/test-blog")
    public String testBlog() {
        return "blog/blog_list";  // 直接返回模板路径
    }
}