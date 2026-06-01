package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
}
