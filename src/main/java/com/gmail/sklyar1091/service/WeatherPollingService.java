package com.gmail.sklyar1091.service;

import com.gmail.sklyar1091.configuration.ModeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherPollingService {

    private final CacheService cacheService;
    private final WeatherService weatherService;
    private final ModeConfig modeConfig;

    @Scheduled(cron = "${sdk-profile.cron}")
    public void updateAllWeatherData() {
        if ("polling".equalsIgnoreCase(modeConfig.getMode())) {
            Set<String> cities = cacheService.getAllCachedCities();
            for (String city : cities) {
                try {
                    weatherService.getWeather(city);
                } catch (Exception e) {
                    log.error("Couldn't update weather's data due to {}", e.getMessage());
                }
            }
        }
    }
}
