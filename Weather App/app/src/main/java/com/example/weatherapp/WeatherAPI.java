package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather?appid=f5c154a245c295bab8a63619f469d082&units=metric")
    Call<OpenWeatherMap> getWeatherWithLocation(@Query("lat") double lat, @Query("lon") double lon);

    @GET("weather?appid=f5c154a245c295bab8a63619f469d082&units=metric")
    Call<OpenWeatherMap> getWeatherWithCityName(@Query("q") String cityName);
}
