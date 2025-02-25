package com.gmail.sklyar1091.service;

import com.gmail.sklyar1091.BaseContextTest;
import com.gmail.sklyar1091.exception.CityNotFoundException;
import com.gmail.sklyar1091.model.WeatherData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WeatherServiceTest extends BaseContextTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CacheService cacheService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    public void whenWeatherDataIsInRedisCacheThenRetrieveFromIt() throws Exception {
        String cityName = "London";
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setName(cityName);
        mockWeatherData.setMain(new WeatherData.Main());
        mockWeatherData.getMain().setTemp(280.32);

        String jsonData = objectMapper.writeValueAsString(mockWeatherData);
        cacheService.cacheCityWeather(cityName, jsonData);

        WeatherData result = weatherService.getWeather(cityName);

        assertNotNull(result);
        assertEquals(cityName, result.getName());
        assertEquals(280.32, result.getMain().getTemp(), 0.01);
    }

    @Test
    public void whenNoWeatherDataCachedThenRetrieveItFromApi() throws Exception {
        String cityName = "London";
        WeatherData mockApiResponse = new WeatherData();
        mockApiResponse.setName(cityName);
        mockApiResponse.setCoord(new WeatherData.Coord());
        mockApiResponse.getCoord().setLat(51.5085);
        mockApiResponse.getCoord().setLon(-0.1257);

        when(restTemplate.getForObject(
                eq("https://api.openweathermap.org/data/2.5/weather?q=London&appid=df03f20f6540e27c8328a16f2363f22f"),
                eq(WeatherData.class)
        )).thenReturn(mockApiResponse);

        WeatherData result = weatherService.getWeather(cityName);

        assertNotNull(result);
        assertEquals(cityName, result.getName());
        assertEquals(51.5085, result.getCoord().getLat(), 0.01);
        assertEquals(-0.1257, result.getCoord().getLon(), 0.01);

        String cachedData = cacheService.getCachedWeatherDataByCity(cityName);
        assertNotNull(cachedData);
        WeatherData cachedWeatherData = objectMapper.readValue(cachedData, WeatherData.class);
        assertNotNull(cachedWeatherData);
        assertEquals(cityName, cachedWeatherData.getName());
    }

    @Test
    public void whenIncorrectCityPassedThenNotFoundException() {
        when(restTemplate.getForObject(
                eq("https://api.openweathermap.org/data/2.5/weather?q=UnknownCity&appid=df03f20f6540e27c8328a16f2363f22f"),
                eq(WeatherData.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Exception exception = assertThrows(CityNotFoundException.class, () -> weatherService.getWeather("UnknownCity"));
        assertEquals("City not found: UnknownCity", exception.getMessage());
    }
}
