package com.gmail.sklyar1091.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sdk-profile")
@Getter
@Setter
public class ModeConfig {

    private String mode;
}
