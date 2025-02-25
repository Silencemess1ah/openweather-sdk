package com.gmail.sklyar1091.service;


import com.gmail.sklyar1091.BaseContextTest;
import com.gmail.sklyar1091.configuration.ModeConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherPollingServiceTest extends BaseContextTest {

    private final CacheService cacheService = Mockito.mock(CacheService.class);
    private final WeatherService weatherService = Mockito.mock(WeatherService.class);
    private final ModeConfig modeConfig = Mockito.mock(ModeConfig.class);

    private final WeatherPollingService weatherPollingService =
            new WeatherPollingService(cacheService, weatherService, modeConfig);

    @Test
    public void whenPollingModeActiveThenRefreshExistingData() {
        Set<String> cities = new HashSet<>(Arrays.asList("London", "Paris"));
        when(cacheService.getAllCachedCities()).thenReturn(cities);
        when(modeConfig.getMode()).thenReturn("polling");

        weatherPollingService.updateAllWeatherData();

        verify(weatherService).getWeather("London");
        verify(weatherService).getWeather("Paris");
    }

    @Test
    public void whenDemandModeOnThenRetrieveSuccessfulData() {
        when(modeConfig.getMode()).thenReturn("onDemand");

        weatherPollingService.updateAllWeatherData();

        verify(weatherService, never()).getWeather(anyString());
    }

    @Test
    public void whenErrorOccurredThenThrownException() {
        Set<String> cities = new HashSet<>(Collections.singletonList("London"));
        when(cacheService.getAllCachedCities()).thenReturn(cities);
        when(modeConfig.getMode()).thenReturn("polling");

        doThrow(new RuntimeException("Simulated error")).when(weatherService).getWeather("London");

        assertDoesNotThrow(weatherPollingService::updateAllWeatherData);
        verify(weatherService, times(1)).getWeather("London");
    }
}
