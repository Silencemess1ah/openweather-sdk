package com.gmail.sklyar1091.controller;

import com.gmail.sklyar1091.model.WeatherData;
import com.gmail.sklyar1091.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/getWeather")
    public ResponseEntity<WeatherData> getWeatherData(@NotNull @RequestParam String cityName) {

        WeatherData weatherData = weatherService.getWeather(cityName);
        return ResponseEntity.ok(weatherData);
    }
}
