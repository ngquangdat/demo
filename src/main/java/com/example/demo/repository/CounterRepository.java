package com.example.demo.repository;

import com.example.demo.entity.Counter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CounterRepository extends JpaRepository<Counter, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Counter c WHERE c.counterName = :name")
    Optional<Counter> findByCounterNameWithLock(String name);
}
