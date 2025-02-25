package com.gmail.sklyar1091.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.sklyar1091.configuration.OpenWeatherConfig;
import com.gmail.sklyar1091.exception.CityNotFoundException;
import com.gmail.sklyar1091.exception.InvalidApiKeyException;
import com.gmail.sklyar1091.exception.WeatherSdkException;
import com.gmail.sklyar1091.model.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WeatherService {

    private final OpenWeatherConfig openWeatherConfig;
    private final RestTemplate restTemplate;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public WeatherService(OpenWeatherConfig openWeatherConfig, RestTemplateBuilder restTemplateBuilder,
                          CacheService cacheService, ObjectMapper objectMapper) {
        this.openWeatherConfig = openWeatherConfig;
        this.restTemplate = restTemplateBuilder.build();
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    public WeatherData getWeather(String cityName) {
        String cachedData = cacheService.getCachedWeatherDataByCity(cityName);
        if (cachedData != null) {
            try {
                return objectMapper.readValue(cachedData, WeatherData.class);
            } catch (Exception e) {
                log.error("Failed to deserialize cached weather data for city {}", cityName, e);
                throw new WeatherSdkException("Failed to process cached weather data");
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put(openWeatherConfig.getCityHolder(), cityName);
        params.put(openWeatherConfig.getApiHolder(), openWeatherConfig.getApiKey());

        try {
            WeatherData weatherData = restTemplate.getForObject(
                    openWeatherConfig.getBaseUrl(),
                    WeatherData.class,
                    params
            );

            String jsonData = objectMapper.writeValueAsString(weatherData);
            cacheService.cacheCityWeather(cityName, jsonData);

            return weatherData;
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while fetching weather data for city {}: {}", cityName, e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException("City not found: " + cityName);
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new InvalidApiKeyException("Invalid API key");
            } else {
                throw new WeatherSdkException("Failed to fetch weather data: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error while fetching weather data for city {}", cityName, e);
            throw new WeatherSdkException("Unexpected error: " + e.getMessage());
        }
    }
}
