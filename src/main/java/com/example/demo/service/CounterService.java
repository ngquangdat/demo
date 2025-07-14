package com.example.demo.service;


import com.example.demo.constant.ResponseCode;
import com.example.demo.entity.Counter;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.CounterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;

    @Transactional
    public Long incrementCounter(String counterName, Long incrementBy) {
        if (incrementBy == null || incrementBy <= 0) {
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }
        Counter counter = counterRepository.findByCounterNameWithLock(counterName)
                .orElse(Counter.builder().counterName(counterName).build());
        counter.setCounterValue(counter.getCounterValue() + incrementBy);
        counterRepository.save(counter);
        return counter.getCounterValue();
    }

    @Transactional
    public Long incrementCounter(String counterName) {
        // Find counter with pessimistic lock
        Counter counter = counterRepository.findByCounterNameWithLock(counterName)
                .orElse(Counter.builder().counterName(counterName).build());

        // Increment counter value
        counter.setCounterValue(counter.getCounterValue() + 1);

        // Save updated counter (lock ensures no concurrent updates)
        counterRepository.save(counter);

        return counter.getCounterValue();
    }

    @Transactional
    public Long getCounterValue(String counterName) {
        Counter counter = counterRepository.findByCounterNameWithLock(counterName)
                .orElse(Counter.builder().counterName(counterName).build());
        return counter.getCounterValue();
    }

    @Transactional
    public Long resetCounter(String counterName) {
        Counter counter = counterRepository.findByCounterNameWithLock(counterName)
                .orElse(Counter.builder().counterName(counterName).build());
        counter.setCounterValue(0L);
        counterRepository.save(counter);
        return counter.getCounterValue();
    }
}