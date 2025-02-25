package com.gmail.sklyar1091.sdk;

import com.gmail.sklyar1091.exception.WeatherSdkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SdkInitializer implements CommandLineRunner {

    private static final Map<String, Boolean> USED_API_KEYS = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) {

        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null || apiKey.isBlank() || apiKey.length() != 32) {
                log.error("Api key - {} is null or length less or more than 32 characters", apiKey);
                throw new IllegalArgumentException("API_KEY is not provided or is wrong!");
            }
            checkApiKeyUniqueness(apiKey);

        } catch (WeatherSdkException e) {
            log.error("Failed to initialize SDK: {}", e.getMessage());
            System.exit(1);
        }
    }

    private void checkApiKeyUniqueness(String apiKey) throws WeatherSdkException {
        if (isApiKeyUsed(apiKey)) {
            log.warn("Api key provided already in use! Please try again with different key!");
            throw new WeatherSdkException("An instance with the same API key already exists.");
        }
        markApiKeyAsUsed(apiKey);
    }

    private boolean isApiKeyUsed(String apiKey) {
        return USED_API_KEYS.containsKey(apiKey);
    }

    private void markApiKeyAsUsed(String apiKey) {
        USED_API_KEYS.put(apiKey, true);
    }

    public static void clearState() {
        USED_API_KEYS.clear();
    }
}
