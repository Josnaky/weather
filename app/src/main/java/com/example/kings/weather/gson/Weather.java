package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hanx3 on 2017/11/18.
 */

public class Weather {

    public String status;
    public Basic basic;
    public Update update;
    public Now now;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @SerializedName("lifestyle")
    public List<Lifestyle> LifestyleList;
}
