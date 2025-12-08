package com.example.blog_system_spring.controller;

import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.service.BlogService;
import com.example.blog_system_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    // ========== 页面跳转方法 ==========

    /**
     * 首页重定向到登录页
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        // 如果已经登录，重定向到博客列表
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "redirect:/blog/list";
        }

        // 检查是否有错误信息
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", "");
        }
        return "auth/blog_login";  // 注意路径：auth/blog_login
    }

    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        // 如果已经登录，重定向到博客列表
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "redirect:/blog/list";
        }

        if (!model.containsAttribute("error")) {
            model.addAttribute("error", "");
        }
        return "auth/blog_register";  // 注意路径：auth/blog_register
    }

    /**
     * 博客列表页面
     */
//    @GetMapping("/blog/list")
//    public String blogList(HttpSession session, Model model) {
//        // 检查登录状态
//        User user = (User) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("username", user.getUsername());
//        model.addAttribute("userId", user.getUserId());
//        // 添加一个空的博客列表（稍后从数据库获取）
//        model.addAttribute("blogs", new ArrayList<>());
//        return "blog/blog_list";  // 注意路径：blog/blog_list
//    }

    // ========== 登录处理 ==========

    /**
     * 用户登录处理
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("登录尝试 - 用户名: " + username + ", 密码: " + password);

        // 参数验证
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "用户名或密码不能为空");
            return "redirect:/login";
        }

        // 验证用户
        User user = userService.login(username, password);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
            return "redirect:/login";
        }

        // 登录成功，设置session
        User sessionUser = new User();
        sessionUser.setUserId(user.getUserId());
        sessionUser.setUsername(user.getUsername());
        session.setAttribute("user", sessionUser);

        System.out.println("登录成功 - 用户ID: " + user.getUserId());
        return "redirect:/blog/list";
    }

    // ========== 注册处理 ==========

    /**
     * 用户注册处理
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 参数验证
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "用户名或密码不能为空");
            return "redirect:/register";
        }

        // 检查用户名是否可用
        if (!userService.isUsernameAvailable(username)) {
            redirectAttributes.addFlashAttribute("error", "用户名已被占用");
            return "redirect:/register";
        }

        try {
            // 注册用户
            User user = userService.register(username, password);

            // 注册成功后自动登录
            User sessionUser = new User();
            sessionUser.setUserId(user.getUserId());
            sessionUser.setUsername(user.getUsername());
            session.setAttribute("user", sessionUser);

            return "redirect:/blog/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "注册失败: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // ========== 注销处理 ==========

    /**
     * 用户注销
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "已成功注销");
        }
        return "redirect:/login";
    }

    // ========== 其他方法 ==========

    /**
     * 检查用户是否登录（JSON API）
     */
    @GetMapping("/api/user/status")
    @ResponseBody
    public Map<String, Object> checkUserStatus(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        User user = (User) session.getAttribute("user");
        if (user != null) {
            result.put("loggedIn", true);
            result.put("username", user.getUsername());
            result.put("userId", user.getUserId());
        } else {
            result.put("loggedIn", false);
        }

        return result;
    }
}