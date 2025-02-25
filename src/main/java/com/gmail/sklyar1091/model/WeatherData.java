package com.gmail.sklyar1091.model;

import lombok.Data;

import java.util.List;

@Data
public class WeatherData {

    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private int dt;
    private Sys sys;
    private int id;
    private String name;
    private int cod;

    @Data
    public static class Coord {

        private double lon;
        private double lat;
    }

    @Data
    public static class Weather {

        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {

        private double temp;
        private int pressure;
        private int humidity;
        private double temp_min;
        private double temp_max;
    }

    @Data
    public static class Wind {

        private double speed;
        private int deg;
    }

    @Data
    public static class Clouds {

        private int all;
    }

    @Data
    public static class Sys {

        private int type;
        private int id;
        private double message;
        private String country;
        private int sunrise;
        private int sunset;
    }
}
