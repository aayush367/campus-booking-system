package com.campus.campusbookingsystem.repository;

import com.campus.campusbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByAccountTypeIgnoreCase(String accountType);

    boolean existsByAccountTypeIgnoreCase(String accountType);
}
