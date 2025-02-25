package com.gmail.sklyar1091.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "open-weather")
@Getter
@Setter
public class OpenWeatherConfig {

    private String apiKey;
    private String baseUrl;
    private String cityHolder;
    private String apiHolder;
}
