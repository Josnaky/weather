package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hanx3 on 2017/11/18.
 */

public class AQI {
    public String aqi;

    public String pm25;

    @SerializedName("qlty")
    public String quality;
}
