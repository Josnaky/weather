package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hanx3 on 2017/11/18.
 */

public class Weather {

    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}