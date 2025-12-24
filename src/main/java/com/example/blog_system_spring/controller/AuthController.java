package com.example.blog_system_spring.controller;

import com.example.blog_system_spring.common.ApiResponse;
import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        // 参数验证
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return ApiResponse.error("用户名或密码不能为空");
        }

        // 验证用户
        User user = userService.login(username, password);
        if (user == null) {
            return ApiResponse.error("用户名或密码错误");
        }

        // 登录成功，设置session
        session.setAttribute("user", user);

        // 返回用户信息（不包含密码）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("username", user.getUsername());

        return ApiResponse.success("登录成功", userInfo);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        // 参数验证
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return ApiResponse.error("用户名或密码不能为空");
        }

        // 检查用户名是否可用
        if (!userService.isUsernameAvailable(username)) {
            return ApiResponse.error("用户名已被占用");
        }

        try {
            // 注册用户
            User user = userService.register(username, password);

            // 注册成功后自动登录
            session.setAttribute("user", user);

            // 返回用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());

            return ApiResponse.success("注册成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户注销
     */
    @GetMapping("/logout")
    public ApiResponse<String> logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
        }
        return ApiResponse.success("已成功注销", null);
    }

    /**
     * 检查用户是否登录
     */
    @GetMapping("/user/status")
    public ApiResponse<Map<String, Object>> checkUserStatus(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        User user = (User) session.getAttribute("user");
        if (user != null) {
            result.put("loggedIn", true);
            result.put("username", user.getUsername());
            result.put("userId", user.getUserId());
        } else {
            result.put("loggedIn", false);
        }

        return ApiResponse.success(result);
    }
}