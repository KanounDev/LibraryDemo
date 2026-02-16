package com.example.library.service;

import com.example.library.model.User;
import com.example.library.model.Role;
import com.example.library.repository.UserRepository;
import com.example.library.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// This service contains the core logic for registration and login.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor injection for dependencies.
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Registers a new user with validation.
    public String register(User user) {
        // Log attempt for debugging.
        System.out.println("Register attempt - username: " + user.username + ", role: " + user.role);

        // Validate branch for LIBRARIAN/ADMIN (required).
        if ((user.role == Role.LIBRARIAN || user.role == Role.ADMIN)
                && (user.branch == null || user.branch.isEmpty())) {
            throw new IllegalArgumentException("Branch is required for librarians and admins");
        }
        // For MEMBER, set branch to null if provided (optional).
        if (user.role == Role.MEMBER) {
            user.branch = null;
        }

        // Hash the password for security.
        user.password = passwordEncoder.encode(user.password);

        try {
            // Save user to MongoDB.
            User saved = userRepository.save(user);
            // Log success.
            System.out.println("REGISTER SUCCESS - User ID: " + saved.id + ", Role: " + saved.role);
            // Generate JWT token.
            return jwtService.generateToken(saved);
        } catch (Exception e) {
            // Log and rethrow error.
            System.err.println("SAVE FAILED: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    // Logs in a user with credentials and branch check.
    public String login(User loginRequest) {
        // Log attempt.
        System.out.println(
                "Login attempt - username: " + loginRequest.username + ", branch: " + loginRequest.branch);

        // Authenticate username/password first using Spring's manager.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password));

        // Find user in DB.
        User foundUser = userRepository.findByUsername(loginRequest.username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Branch check for LIBRARIAN/ADMIN.
        if (foundUser.role == Role.LIBRARIAN || foundUser.role == Role.ADMIN) {
            if (loginRequest.branch == null || loginRequest.branch.isBlank()) {
                throw new IllegalArgumentException("Branch is required for librarians and admins");
            }
            if (!loginRequest.branch.equalsIgnoreCase(foundUser.branch)) {
                throw new BadCredentialsException("Invalid branch for this user");
            }
        } else {
            // For MEMBER: reject if branch was sent.
            if (loginRequest.branch != null && !loginRequest.branch.isBlank()) {
                throw new IllegalArgumentException("Branch not applicable for members");
            }
        }

        // Log success.
        System.out.println("LOGIN SUCCESS - User ID: " + foundUser.id + ", Role: " + foundUser.role + ", Branch: "
                + foundUser.branch);

        // Generate JWT token.
        return jwtService.generateToken(foundUser);
    }
}