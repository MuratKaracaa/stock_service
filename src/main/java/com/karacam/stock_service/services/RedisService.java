package com.karacam.stock_service.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private SetOperations<String, Object> setOperations;

    @PostConstruct
    public void init(){
        setOperations = redisTemplate.opsForSet();
    }

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate_){
        this.redisTemplate = redisTemplate_;
    }

    public boolean checkAndSet(String setKey, String... values){
        Long insertedCount = setOperations.add(setKey, values);
        return insertedCount > 0;

    }

}
