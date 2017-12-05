package com.example.kings.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.kings.weather.gson.AirQuality;
import com.example.kings.weather.gson.BingPicUrl;
import com.example.kings.weather.gson.Weather;
import com.example.kings.weather.util.HttpUtil;
import com.example.kings.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

     @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        updateWeather();
        updateAirQuality();
        updateBingPic();
         AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
         int anHour = 1 * 60 * 60 * 1000;
         Long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
         Intent i = new Intent(this, AutoUpdateService.class);
         PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
         manager.cancel(pi);
         manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
         return super.onStartCommand(intent, flags, startId);
     }


    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "https://free-api.heweather.com/s6/weather?location="+weatherId+
                    "&key=5d63dbbba8944757994870c7260299b5";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if(weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新天气质量信息
     */
    private void updateAirQuality() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String airString = prefs.getString("air", null);

        if (airString != null) {
            //有缓存时直接解析天气质量信息
            AirQuality airQuality = Utility.handleAirQualityResponse(airString);
            String weatherId = airQuality.basic.weatherId;

            String airUrl = "https://free-api.heweather.com/s6/air/now?location=" + weatherId +
                    "&key=5d63dbbba8944757994870c7260299b5";
            HttpUtil.sendOkHttpRequest(airUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    AirQuality airQuality = Utility.handleAirQualityResponse(responseText);
                    if (airQuality != null && "ok".equals(airQuality.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).
                                edit();
                        editor.putString("air", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic(){
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=" +
                "1&nc=1512054214058&pid=hp";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                BingPicUrl bingPicUrl = Utility.handleBingPicUrlResponse(responseText);
                String bingPic = "https://cn.bing.com"+ bingPicUrl.url;
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }
}
