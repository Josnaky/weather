package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kings on 2017/11/22.
 */

public class Lifestyle {
    @SerializedName("brf")
    public String state;

    public String type;

    @SerializedName("txt")
    public String suggestion;

}
