package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hanx3 on 2017/11/18.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
