package com.example.ankur.pollutionandweathermonitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HeatMap extends AppCompatActivity {
    public final static String EXTRA_MESSAGE_CONDITION = "condition";
    public static final String EXTRA_MESSAGE_LONGITUTE = "longitude";
    public static final String EXTRA_MESSAGE_LATITUTE = "latitude";
    public static final String EXTRA_MESSAGE_PAYLOAD = "temperature";
    private String onReceived;
    private static String payload;
    private double longitude;
    private double latitude;

    private LoginButton loginButton;
    private GoogleMap mMap;
    // Create the gradient.
    int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };

    float[] startPoints = {
            0.2f, 1f
    };

    Gradient gradient = new Gradient(colors, startPoints);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTextSize(15);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        onReceived = intent.getStringExtra(Weather.EXTRA_MESSAGE_CONDITION);

        if (onReceived != null) {
            longitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(Weather.EXTRA_MESSAGE_PAYLOAD);
            //Log.d("HeatMap", payload);

        }
        onReceived = intent.getStringExtra(Pollution.EXTRA_MESSAGE_CONDITION);
        if (onReceived != null) {
            longitude = intent.getExtras().getDouble(Pollution.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(Pollution.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(Pollution.EXTRA_MESSAGE_PAYLOAD);
            //Log.d("HeatMap", payload);

        }
        if (onReceived == null) {
            Log.d("HeatMap", "onReceived is null");
            longitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(DisplayInformation.EXTRA_MESSAGE_PAYLOAD);
            // Log.d("HeatMap", payload);

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded();
        addHeatMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        addHeatMap();
    }

    public void RedirectToHome(View view) {
        // Log.d("Weather", String.valueOf(longitude));
        //Log.d("Weather", String.valueOf(latitude));
        Intent intent = new Intent(this, DisplayInformation.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        startActivity(intent);
    }

    public void RedirectToWeather(View view) {
        Intent intent = new Intent(this, Weather.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        startActivity(intent);
    }

    public void RedirectToPollution(View view) {
        Intent intent = new Intent(this, Test.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        startActivity(intent);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // Position the camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-38.05, 144.168), 6));
            }
        }
    }

    /**
     * Add a simple heat map to the map
     */
    private void addHeatMap() {
        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of pollutant locations.
        try {
            list = readItems(R.raw.pollutants_with_location);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the pollutants.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().data(list).gradient(gradient).build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        @SuppressWarnings("resource")
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }

        return list;
    }
}