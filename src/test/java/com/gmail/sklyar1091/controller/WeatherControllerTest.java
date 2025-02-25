package com.gmail.sklyar1091.controller;

import com.gmail.sklyar1091.BaseContextTest;
import com.gmail.sklyar1091.exception.CityNotFoundException;
import com.gmail.sklyar1091.exception.InvalidApiKeyException;
import com.gmail.sklyar1091.model.WeatherData;
import com.gmail.sklyar1091.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WeatherControllerTest extends BaseContextTest {

    @MockitoBean
    private WeatherService weatherService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenValidCityNamePassedThenSuccess() throws Exception {
        String cityName = "London";
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setName(cityName);
        mockWeatherData.setMain(new WeatherData.Main());
        mockWeatherData.getMain().setTemp(280.32);

        when(weatherService.getWeather(cityName)).thenReturn(mockWeatherData);

        mockMvc.perform(get("/v1/getWeather")
                        .param("cityName", cityName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(cityName))
                .andExpect(jsonPath("$.main.temp").value(280.32));
    }

    @Test
    public void whenCityDoesNotExistThenNotFoundError() throws Exception {
        String cityName = "UnknownCity";
        when(weatherService.getWeather(cityName)).thenThrow(new CityNotFoundException("City not found: " + cityName));

        mockMvc.perform(get("/v1/getWeather")
                        .param("cityName", cityName))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("City not found")));
    }

    @Test
    public void whenInvalidApiKeyPassedThenInvalidKeyError() throws Exception {
        String cityName = "London";
        when(weatherService.getWeather(cityName)).thenThrow(new InvalidApiKeyException("Invalid API key"));

        mockMvc.perform(get("/v1/getWeather")
                        .param("cityName", cityName))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid API key")));
    }

    @Test
    public void whenUnexpectedErrorOccurredThenExceptionThrown() throws Exception {
        String cityName = "Paris";
        when(weatherService.getWeather(cityName)).thenThrow(new RuntimeException("Simulated error"));

        mockMvc.perform(get("/v1/getWeather")
                        .param("cityName", cityName))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Unexpected error")));
    }
}
