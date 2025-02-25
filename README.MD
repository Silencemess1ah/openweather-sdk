# OpenWeather SDK

## Описание
Этот SDK предоставляет простой интерфейс для работы с API OpenWeatherMap. Он поддерживает кэширование данных в Redis, 
два режима работы (on-demand и polling) и обеспечивает надёжную обработку ошибок.

---

## Требования

- **Java 17+**
- **Maven или Gradle**
- **Docker** (для запуска Redis и приложения)
- **API-ключ OpenWeatherMap** (получить можно [здесь](https://openweathermap.org/api))

---

## Установка

1. **Склонируйте репозиторий:**
   ```bash
   git clone https://github.com/Silencemess1ah/openweather-sdk.git
   cd openweather-sdk
   ```
2. **Установите зависимости**
    ```bash
   chmod +x build.sh
   ./build.sh
    ```
3. **Создание JAR-файла**
   ```bash
   ./gradlew bootJar
   ```
   Jar файл будет доступен по ```build/libs/openweather-0.0.1-SNAPSHOT.jar```

## Запуск
```bash
chmod +x run.sh
./run.sh *your_api_key* *sdk_mode*
```
1. Нужен Docker, поднимется контейнер с Redis и приложением openweather
2. Для остановки используйте скрипт :
   ```bash
   chmod +x stop.sh
   ./stop.sh
   ```
3. Для тестов используйте .env файл для передачи аргументов окружение
   ```API_KEY=*YOUR_API_KEY*
      SDK_MODE=*PREFERED_SDK_MODE*
   ```
   ```bash
   chmod +x test.sh
   ./test.sh
   ```

## API

```GET /v1/getWeather?cityName={cityName}```

```cityname: название города обязательный параметр```

**Пример запроса**

```curl "http://localhost:8088/v1/getWeather?cityName=London"```

***Пример ответа***

```json
{
  "coord": {
    "lon": -0.13,
    "lat": 51.51
  },
  "weather": [
    {
      "id": 300,
      "main": "Drizzle",
      "description": "light intensity drizzle",
      "icon": "09d"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 280.32,
    "pressure": 1012,
    "humidity": 81
  },
  "visibility": 10000,
  "wind": {
    "speed": 4.1,
    "deg": 80
  },
  "clouds": {
    "all": 90
  },
  "dt": 1485789600,
  "sys": {
    "type": 1,
    "id": 5091,
    "country": "GB",
    "sunrise": 1485762037,
    "sunset": 1485794875
  },
  "id": 2643743,
  "name": "London",
  "cod": 200
}
```

***Возможные ошибки***

### HTTP 404 Not Found: Если город не найден.

```json
{
  "status": 404,
  "message": "City not found: UnknownCity"
}
```

### HTTP 401 Unauthorized: Если API-ключ недействителен.

```json
{
  "status": 401,
  "message": "Invalid API key"
}
```

### HTTP 500 Internal Server Error: непредвиденные ошибки

```json
{
  "status": 500,
  "message": "Unexpected error: Simulated server error"
}
```