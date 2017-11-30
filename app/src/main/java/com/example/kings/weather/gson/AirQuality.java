package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hanx3 on 2017/11/27.
 */

public class AirQuality {
    public String status;

    @SerializedName("air_now_city")
    public AQI air;
}
