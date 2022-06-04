package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationCityActivity extends AppCompatActivity {

    TextView locationCityTextview, locationTemperatureTextview, locationWeatherStatusTextview, locationHumidityTextview, locationMaxTempTextview, locationMinTempTextview
            , locationPressureTextview, locationWindSpeedTextview;

    ImageView locationStatusImageview;

    EditText cityLocation;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_city);

        locationCityTextview = findViewById(R.id.location_textview_city);
        locationTemperatureTextview = findViewById(R.id.location_textview_temperature);
        locationWeatherStatusTextview = findViewById(R.id.location_textview_weather_status);
        locationHumidityTextview = findViewById(R.id.location_textview_humidity);
        locationMaxTempTextview = findViewById(R.id.location_textview_max_temp);
        locationMinTempTextview = findViewById(R.id.location_textview_min_temp);
        locationPressureTextview = findViewById(R.id.location_textview_pressure);
        locationWindSpeedTextview = findViewById(R.id.location_textview_wind_speed);
        locationStatusImageview = findViewById(R.id.location_imageview_weather_status);

        cityLocation = findViewById(R.id.edittext_city_location);
        search = findViewById(R.id.location_search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityLocation.getText().toString();
                getWeatherData(cityName);

                cityLocation.setText(" ");

            }
        });
    }

    public void getWeatherData(String cityName){

        WeatherAPI weatherAPI = WeatherRetrofit.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call =weatherAPI.getWeatherWithCityName(cityName);
        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {

                if (response.isSuccessful()){
                    locationCityTextview.setText(response.body().getName() + ", " + response.body().getSys().getCountry());
                    locationTemperatureTextview.setText(response.body().getMain().getTemp() + "°C");
                    locationWeatherStatusTextview.setText(response.body().getWeather().get(0).getDescription());
                    locationHumidityTextview.setText(response.body().getMain().getHumidity() + "%");
                    locationMaxTempTextview.setText(response.body().getMain().getTempMax() + "°C");
                    locationMinTempTextview.setText(response.body().getMain().getTempMin() + "°C");
                    locationPressureTextview.setText("" +response.body().getMain().getPressure());
                    locationWindSpeedTextview.setText(""+response.body().getWind().getSpeed());

                    String icon = response.body().getWeather().get(0).getIcon();
                    Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").placeholder(R.drawable.ic_launcher_background)
                            .into(locationStatusImageview);

                }else {
                    Toast.makeText(LocationCityActivity.this, "Wrong city name, try again", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                Toast.makeText(LocationCityActivity.this, "Response Fails", Toast.LENGTH_SHORT).show();
                Log.d("error",t.getLocalizedMessage());

            }
        });
    }
}