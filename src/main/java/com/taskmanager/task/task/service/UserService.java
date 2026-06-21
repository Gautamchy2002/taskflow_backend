package com.taskmanager.task.task.service;

import com.taskmanager.task.task.entity.User;
import com.taskmanager.task.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User signup(User user){
     if (userRepository.findByUsername(user.getUsername()).isPresent()) {
         throw new RuntimeException("Username already exists");
     }

     if (userRepository.findByEmail(user.getEmail()).isPresent()){
         throw new RuntimeException("Email already exists");
     }

     String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new ArrayList<>());
            user.getRoles().add("USER");
        }

        User savedUser = userRepository.save(user);
        emailService.sendEmail(
                savedUser.getEmail(),
                "Welcome to TaskFlow - Login Details",
                "Hello " + savedUser.getName() + ",\n\n" +
                        "Your TaskFlow account has been created successfully.\n\n" +
                        "Login Details:\n" +
                        "Username: " + savedUser.getUsername() + "\n" +
                        "Password: " + rawPassword + "\n\n" +
                        "Please login and start managing your tasks.\n\n" +
                        "Thanks,\n" +
                        "TaskFlow Team"
        );
        return savedUser;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}
