package com.example.library.controller;

import com.example.library.model.User;
import com.example.library.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

// This controller handles authentication routes like register, login, and get current user.
// @RestController means it returns JSON responses.
// @RequestMapping sets base path.
// @CrossOrigin allows requests from Angular frontend.
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthService authService;

    // Constructor injection for AuthService.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // GET /api/auth/me: Returns the current authenticated user.
    // Uses Spring's Authentication object from security context.
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication auth) {
        User current = (User) auth.getPrincipal(); // Cast principal to User.
        return ResponseEntity.ok(current); // Return 200 OK with user.
    }

    // POST /api/auth/register: Registers a new user.
    // Expects User object in body.
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        try {
            // Call service to register and get token.
            String token = authService.register(user);
            Map<String, String> response = new HashMap<>();
            response.put("token", token); // Prepare response with token.
            return ResponseEntity.ok(response); // 200 OK.
        } catch (Exception e) {
            // Handle errors, return 400 with message.
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/auth/login: Logs in a user.
    // Expects User object (with username, password, branch if needed).
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        try {
            // Call service to login and get token.
            String token = authService.login(user);
            Map<String, String> response = new HashMap<>();
            response.put("token", token); // Prepare response.
            return ResponseEntity.ok(response); // 200 OK.
        } catch (BadCredentialsException | IllegalArgumentException e) {
            // 401 for bad creds or invalid branch.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // 500 for other errors.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed"));
        }
    }
}