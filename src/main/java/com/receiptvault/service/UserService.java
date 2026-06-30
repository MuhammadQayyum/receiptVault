package com.receiptvault.service;

import com.receiptvault.entity.User;
import com.receiptvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User registerUser(String fullName, String username, String password, String role) {
        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role != null ? role : "USER");
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void toggleUserValidity(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setIsValid(!user.getIsValid());
            userRepository.save(user);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}