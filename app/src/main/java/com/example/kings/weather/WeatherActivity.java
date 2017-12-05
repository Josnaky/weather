package com.example.kings.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kings.weather.gson.AirQuality;
import com.example.kings.weather.gson.BingPicUrl;
import com.example.kings.weather.gson.Forecast;
import com.example.kings.weather.gson.Lifestyle;
import com.example.kings.weather.gson.Weather;
import com.example.kings.weather.service.AutoUpdateService;
import com.example.kings.weather.util.HttpUtil;
import com.example.kings.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements
        ChooseAreaFragment.SelectedWeatherIdListener {

    public DrawerLayout drawerLayout;
    private Button navButton;

    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private  TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private LinearLayout suggestionLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView quality;

    private ImageView bingPicImg;
    private String weatherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        suggestionLayout = (LinearLayout) findViewById(R.id.suggestion_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        quality = (TextView) findViewById(R.id.quality);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String airString = prefs.getString("air", null);
        String bingPic = prefs.getString("bing_pic", null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
            if(airString != null){
                AirQuality air = Utility.handleAirQualityResponse(airString);
                showAirQualityInfo(air);
            }
        }else{
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
            requestAirQuality(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                requestAirQuality(weatherId);
                //getBingPicUrl();
                loadBingPic();
            }
        });

//        loadBingPic();
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }


    /**
     * 从ChooseAreaFragment获取重新选择的天气ID
     * @param weatherId
     */
    public void getSelectedWeatherId(String weatherId){
        this.weatherId = weatherId;
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location="+weatherId+
                  "&key=5d63dbbba8944757994870c7260299b5";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                            }
                        swipeRefresh.setRefreshing(false);
                        }
                });
            }
        });
//        loadBingPic();
    }

    /**
     * 根据weatherId查询天气状况
     */
    public void requestAirQuality(final String weatherId){
        String airUrl = "https://free-api.heweather.com/s6/air/now?location="+ weatherId +
                "&key=5d63dbbba8944757994870c7260299b5";
        HttpUtil.sendOkHttpRequest(airUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取空气质量失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final AirQuality air = Utility.handleAirQualityResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(air != null && "ok".equals(air.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("air",responseText);
                            editor.apply();
                            showAirQualityInfo(air);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取空气质量失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=" +
                "1&nc=1512054214058&pid=hp";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final BingPicUrl bingPicUrl = Utility.handleBingPicUrlResponse(responseText);
                final String bingPic = "https://cn.bing.com"+ bingPicUrl.url;
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 处理并显示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.tmp_max);
            minText.setText(forecast.tmp_min);
            forecastLayout.addView(view);
        }
        String text;
        suggestionLayout.removeAllViews();
        for(Lifestyle lifestyle : weather.LifestyleList){
            if("comf".equals(lifestyle.type)){
                text = "舒适指数";
            }else if("drsg".equals(lifestyle.type)){
                text = "穿衣指数";
            }else if("sport".equals(lifestyle.type)){
                text = "运动指数" ;
            }else if ("cw".equals(lifestyle.type)){
                text = "洗车指数";
            }else{
                continue;
            }
            View view = LayoutInflater.from(this).inflate(R.layout.suggestion_item,
                    suggestionLayout, false);
            TextView stateText = (TextView) view.findViewById(R.id.state_text);
            TextView suggestText = (TextView) view.findViewById(R.id.suggestion_text);
            stateText.setText(text);
            suggestText.setText(lifestyle.suggestion);
            suggestionLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示空气质量信息
     */
    public void showAirQualityInfo(AirQuality airQuality){
        aqiText.setText(airQuality.air.aqi);
        pm25Text.setText(airQuality.air.pm25);
        quality.setText(airQuality.air.quality);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
