FROM openjdk:17-jdk-slim
ARG API_KEY
ARG SDK_MODE
COPY build/libs/openweather-0.0.1-SNAPSHOT.jar openweather.jar
ENV API_KEY=${API_KEY}
ENV SDK_MODE=${SDK_MODE}
CMD ["java", "-Dsdk-profile.mode=${SDK_MODE}", "-Dopen-weather.apiKey=${API_KEY}", "-jar", "openweather.jar"]