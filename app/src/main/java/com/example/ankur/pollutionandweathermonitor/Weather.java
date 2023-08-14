package com.example.rupam.pollutionandweathermonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;

public class Weather extends AppCompatActivity {
    public final static String EXTRA_MESSAGE_LONGITUTE = "longitude";
    public final static String EXTRA_MESSAGE_LATITUTE = "latitude";
    public final static String EXTRA_MESSAGE_CONDITION = "condition";
    public static final String EXTRA_MESSAGE_PAYLOAD ="temperature";

    private LoginButton loginButton;
    private double longitude;
    private double latitude;
    private String onReceived;
    private static String payload = "init";
    private String[] temperture;
    private TextView temperatureTextView;
    private ImageView cloudImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTextSize(15);
        Intent intent = getIntent();
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        cloudImage = (ImageView) findViewById(R.id.cloudImage);
        onReceived = intent.getStringExtra(Pollution.EXTRA_MESSAGE_CONDITION);

        if(onReceived != null){
            longitude = intent.getExtras().getDouble(Test.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(Test.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(Test.EXTRA_MESSAGE_PAYLOAD);
        }
        onReceived = intent.getStringExtra(HeatMap.EXTRA_MESSAGE_CONDITION);
        if(onReceived != null){
            longitude = intent.getExtras().getDouble(HeatMap.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(HeatMap.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(HeatMap.EXTRA_MESSAGE_PAYLOAD);
        }
        if(onReceived == null){
            Log.d("Weather", "onReceived is null");
            longitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(DisplayInformation.EXTRA_MESSAGE_PAYLOAD);
            //Log.d("Weather", payload);

        }
        if(payload != null) {
            temperture = payload.split(",");
            temperatureTextView.setText(temperture[0]);
            /*
            if(Integer.parseInt(temperture[0]) < 5) {
                cloudImage.setBackgroundResource(R.drawable.weather_snowy);
            }
            else if(Integer.parseInt(temperture[0]) < 20 && Integer.parseInt(temperture[0]) >= 5)  {
                cloudImage.setBackgroundResource(R.drawable.weather_cloudy);
            }
            else {
                cloudImage.setBackgroundResource(R.drawable.weather_rainy);
            }*/
        }



        //payload = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_PAYLOAD);
/*
        Log.d("Weather" , String.valueOf(longitude));
        Log.d("Weather" , String.valueOf(latitude));
        Log.d("Weather" , String.valueOf(payload));

*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void RedirectToHome(View view){
        Log.d("Weather", String.valueOf(longitude));
        Log.d("Weather", String.valueOf(latitude));
        Intent intent = new Intent(this, DisplayInformation.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        startActivity(intent);
    }
    public void RedirectToPollution(View view){
        Intent intent = new Intent(this, Test.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE,latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        startActivity(intent);
    }
    public void RedirectToHeatMap(View view){
        Intent intent = new Intent(this, HeatMap.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        startActivity(intent);
    }


}
