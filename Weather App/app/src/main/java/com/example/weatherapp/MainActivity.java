package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView cityTextview, temperatureTextview, weatherStatusTextview, humidityTextview, maxTempTextview, minTempTextview
            , pressureTextview, windSpeedTextview;

    ImageView statusImageview;
    FloatingActionButton fab;

    LocationManager locationManager;
    LocationListener locationListener;

    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextview = findViewById(R.id.textview_city);
        temperatureTextview = findViewById(R.id.textview_temperature);
        weatherStatusTextview = findViewById(R.id.textview_weather_status);
        humidityTextview = findViewById(R.id.textview_humidity);
        maxTempTextview = findViewById(R.id.textview_max_temp);
        minTempTextview = findViewById(R.id.textview_min_temp);
        pressureTextview = findViewById(R.id.textview_pressure);
        windSpeedTextview = findViewById(R.id.textview_wind_speed);
        statusImageview = findViewById(R.id.imageview_weather_status);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LocationCityActivity.class);
                startActivity(intent);

            }
        });

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                Log.e("Lat: ",  String.valueOf(lat));
                Log.e("Lon: ",  String.valueOf(lon));

                getWeatherData(lat, lon);
            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, locationListener);
        }

    }

    public void getWeatherData(double lat, double lon){

        WeatherAPI weatherAPI = WeatherRetrofit.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call =weatherAPI.getWeatherWithLocation(lat, lon);
        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {

                cityTextview.setText(response.body().getName() + ", " + response.body().getSys().getCountry());
                temperatureTextview.setText(response.body().getMain().getTemp() + "°C");
                weatherStatusTextview.setText(response.body().getWeather().get(0).getDescription());
                humidityTextview.setText(response.body().getMain().getHumidity() + "%");
                maxTempTextview.setText(response.body().getMain().getTempMax() + "°C");
                minTempTextview.setText(response.body().getMain().getTempMin() + "°C");
                pressureTextview.setText("" +response.body().getMain().getPressure());
                windSpeedTextview.setText(""+response.body().getWind().getSpeed());

                String icon = response.body().getWeather().get(0).getIcon();
                Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").placeholder(R.drawable.ic_launcher_background)
                        .into(statusImageview);


            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Response Fails", Toast.LENGTH_SHORT).show();
                Log.d("error",t.getLocalizedMessage());

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length>0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, locationListener);
        }
    }
}