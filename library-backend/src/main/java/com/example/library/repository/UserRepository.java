package com.example.library.repository;

import com.example.library.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// This interface provides CRUD operations for User documents in MongoDB.
// No need to implement methods—Spring Data handles it.
public interface UserRepository extends MongoRepository<User, String> {
    
    // Custom query method: Find user by username.
    // Returns Optional to handle cases where user might not exist.
    Optional<User> findByUsername(String username);
}