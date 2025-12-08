package com.example.blog_system_spring.service;

import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ========== 对应原UserDao的方法 ==========

    /**
     * 插入/注册用户
     */
    @Transactional
    public User insertUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        return userRepository.save(user);
    }

    /**
     * 根据用户名查找用户（用于登录）
     */
    public User selectByName(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    /**
     * 根据用户ID查找用户
     */
    public User selectById(Integer userId) {
        if (userId == null) {
            return null;
        }
        Optional<User> userOptional = userRepository.findByUserId(userId);
        return userOptional.orElse(null);
    }

    // ========== 用户认证相关方法 ==========

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码（明文）
     * @return 验证成功的用户对象，失败返回null
     */
    public User login(String username, String password) {
        User user = selectByName(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @return 注册成功的用户对象
     */
    @Transactional
    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        // 直接保存明文密码（仅用于测试）
        user.setPassword(password);
        return insertUser(user);
    }

    // ========== 其他实用方法 ==========

    /**
     * 检查用户名是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(User user) {
        // 确保用户存在
        Optional<User> existingUser = userRepository.findByUserId(user.getUserId());
        if (existingUser.isPresent()) {
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("用户不存在");
    }

    /**
     * 删除用户
     */
    @Transactional
    public boolean deleteUser(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}