package com.example.library.model;

// Enum defines fixed roles for users.
public enum Role {
    MEMBER,    // Regular user, no branch needed.
    LIBRARIAN, // Manages books in a branch.
    ADMIN      // Oversees the system in a branch.
}