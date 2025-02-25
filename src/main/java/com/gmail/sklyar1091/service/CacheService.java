package com.gmail.sklyar1091.service;

import com.gmail.sklyar1091.configuration.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    private final RedisProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public void cacheCityWeather(String cityName, String jsonWeatherData) {
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(redisProperties.getCacheKey(), cityName, score);
        redisTemplate.opsForValue().set(cityName, jsonWeatherData, Duration.ofMinutes(redisProperties.getTtl()));
        setCacheMaxSize();
    }

    public String getCachedWeatherDataByCity(String cityName) {
        return redisTemplate.opsForValue().get(cityName);
    }

    private void setCacheMaxSize() {
        long size = redisTemplate.opsForZSet().zCard(redisProperties.getCacheKey());
        if (size > redisProperties.getCacheSize()) {
            Set<ZSetOperations.TypedTuple<String>> cities = redisTemplate.opsForZSet()
                    .rangeWithScores(redisProperties.getCacheKey(), 0, redisProperties.getCacheSize() - 1);
            if (!cities.isEmpty()) {
                double oldestScore = cities.stream()
                        .mapToDouble(ZSetOperations.TypedTuple::getScore)
                        .min()
                        .orElse(0);
                redisTemplate.opsForZSet().removeRangeByScore(redisProperties.getCacheKey(), 0, oldestScore);
            }
        }
    }

    public Set<String> getAllCachedCities() {
        return redisTemplate.opsForZSet().reverseRange(redisProperties.getCacheKey(), 0, -1);
    }
}
