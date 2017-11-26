package com.example.kings.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hanx3 on 2017/11/18.
 */

public class Forecast {
    public String date;

    public String tmp_max;
    public String tmp_min;

    @SerializedName("cond_txt_d")
    public String info;

}
