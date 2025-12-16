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

    @Transactional
    public User insertUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        return userRepository.save(user);
    }

    public User selectByName(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }


    public User selectById(Integer userId) {
        if (userId == null) {
            return null;
        }
        Optional<User> userOptional = userRepository.findByUserId(userId);
        return userOptional.orElse(null);
    }


    public User login(String username, String password) {
        User user = selectByName(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    @Transactional
    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return insertUser(user);
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    @Transactional
    public User updateUser(User user) {
        // 确保用户存在
        Optional<User> existingUser = userRepository.findByUserId(user.getUserId());
        if (existingUser.isPresent()) {
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("用户不存在");
    }

    @Transactional
    public boolean deleteUser(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}