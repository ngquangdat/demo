package com.example.demo.repository;

import com.example.demo.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Account, Integer> {
    @Query("SELECT u FROM Account u WHERE u.username = :username")
    Optional<Account> getByUsername(@Param("username") String username);
}
