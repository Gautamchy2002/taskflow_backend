package com.taskmanager.task.task.controller;

import com.taskmanager.task.task.dto.AuthResponse;
import com.taskmanager.task.task.entity.PasswordResetToken;
import com.taskmanager.task.task.entity.User;
import com.taskmanager.task.task.security.JwtUtil;
import com.taskmanager.task.task.service.EmailService;
import com.taskmanager.task.task.service.PasswordResetService;
import com.taskmanager.task.task.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("Taskflow Auth is running");
    }

    @GetMapping("/test-mail")
    public ResponseEntity<String> testMail() {
        emailService.sendEmail(
                "avinash.ojha.cs24@iilmcet.ac.in",
                "TaskFlow Test Mail",
                "Email service is working successfully"
        );

        return ResponseEntity.ok("Mail sent successfully");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user){
        try {
          User savedUser = userService.signup(user);
          savedUser.setPassword(null);
          return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try {
            User loggedInUser = userService.login(user.getUsername(), user.getPassword());

            String token = jwtUtil.generateToken(loggedInUser.getUsername());
            AuthResponse response = new AuthResponse(token, loggedInUser.getUsername(), loggedInUser.getEmail(), loggedInUser.getRoles());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            passwordResetService.sendResetLink(email);

            return ResponseEntity.ok("Password reset link sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");

            passwordResetService.resetPassword(token, newPassword);

            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
