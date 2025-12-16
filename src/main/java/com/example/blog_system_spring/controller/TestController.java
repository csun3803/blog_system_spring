package com.example.blog_system_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "模板引擎工作正常！");
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "test";
    }

    @GetMapping("/test-login")
    public String testLogin() {
        return "auth/blog_login";
    }

    @GetMapping("/test-blog")
    public String testBlog() {
        return "blog/blog_list";
    }
}