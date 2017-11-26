package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hanx3 on 2017/11/18.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;


    @SerializedName("cond_txt")
    public String info;

}
