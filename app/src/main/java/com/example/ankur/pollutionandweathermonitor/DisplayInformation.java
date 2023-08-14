package com.example.rupam.pollutionandweathermonitor;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;


public class DisplayInformation extends AppCompatActivity   implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    /*loginButton is to facilitate facebook logout */
    private LoginButton loginButton;
    /*callbackManager is for the callback functions for facebook logout*/
    private CallbackManager callbackManager;
    private TextView textView;
    private TextView textviewPollutants;
    private TextView locationView;
    private String firstName;
    /* locationPermissionStatus is to store the permission status of the ACCESS_FINE_LOCATION */
    private String locationPermissionStatus = "";
    private String LOGTAG = DisplayInformation.class.getSimpleName();
    private AccessTokenTracker accessTokenTracker;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location mLastLocation;
    private static double latitude;
    private static double longitude;
    private GoogleMap mMap;
    MqttAndroidClient client;
    private Context context;
    private String intentWeatherCondition;
    public static final String EXTRA_MESSAGE_LONGITUTE ="longitude";
    public static final String EXTRA_MESSAGE_LATITUTE ="latitude";
    public static final String EXTRA_MESSAGE_PAYLOAD ="temperature";
    private static String payload;
    private double temperature;
    private static boolean  onConnected = false;
    private boolean mqttStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_display_information);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTextSize(15);
        try{
            loginButton.setReadPermissions("user_friends");

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v(LOGTAG, "successfully connected to facebook");
                }
                @Override
                public void onCancel() {
                    Log.v(LOGTAG, " connection to facebook cancelled");
                }
                @Override
                public void onError(FacebookException exception) {
                    Log.v(LOGTAG, "Error on  connection to facebook");
                }
            });
        }catch (Exception e){
            Log.v(LOGTAG, "Error in the loginButton facebook");
            e.printStackTrace();
        }
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    Log.v(LOGTAG, "Logged out: Redirecting to facebook");
                    RedirectToMainActivity();
                }
            }
        };
        context = getApplicationContext();
        connect();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //textView = (TextView) findViewById(R.id.textView) ;
        //textviewPollutants = (TextView) findViewById(R.id.textView3);
        locationView = (TextView) findViewById(R.id.location);
        //Get the firstname of the user  logged in through facebook
        //GetFacebookFirstName();
        Intent intent = getIntent();
        //Get the permission status of the ACCESS_FINE_LOCATION
        locationPermissionStatus = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_LOCATION_PERMISSION);
        firstName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_FIRSTNAME);
        //Welcome message
        //textView.setTextSize(20);
        //textView.setText("Welcome " + firstName + "!");

        //longitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LONGITUTE);
        //latitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LATITUTE);
        intentWeatherCondition = intent.getStringExtra(Weather.EXTRA_MESSAGE_CONDITION);
       if(intentWeatherCondition !=null){
            Log.d(LOGTAG, "Returned from the Weather Activity");
            loadMap();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //The following is the Search Utility for Location Search
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.v(LOGTAG, "Places: " + place.getName() + " " + place.getId() + " " + place.getLatLng().latitude);
                longitude = place.getLatLng().longitude;
                latitude = place.getLatLng().latitude;
                String message = "LATITUTE: " + String.valueOf(latitude) + " LONGITUDE: " + String.valueOf(longitude);
                locationView.setText(message);
                //After the location is selected by the user, the maps is loaded,and the request to get
                //the parameters for that particular location is sent to the server
                loadMap();
                publish();
                subscribe();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.v(LOGTAG, "An error occurred: " + status);
            }
        });

    }
    //The following function is used to send the user back to the mainActivity after logout.
    private void RedirectToMainActivity() {
        Log.v(LOGTAG, "RedirectToMainActivity called");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void RedirectToWeather(View view){
        Intent intentWeather = new Intent(this, Weather.class);
        intentWeather.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intentWeather.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intentWeather.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);


        startActivity(intentWeather);
    }
    public void RedirectToPollution(View view){
        Intent intentWeather = new Intent(this, Test.class);
        intentWeather.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intentWeather.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intentWeather.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        startActivity(intentWeather);
    }
    public void RedirectToHeatMap(View view){
        Intent intentWeather = new Intent(this, HeatMap.class);
        intentWeather.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intentWeather.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intentWeather.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        startActivity(intentWeather);
    }

    @Override
    protected void onStart(){
        super.onStart();
        //This is to start the Google Play Services
        googleApiClient.connect();

        //locationView.setText("This is a placeholder for the user's last known location");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //The following function is a callback when the Google Play Services is successfully connected
    @Override
    public void onConnected(Bundle bundle) {
        if(intentWeatherCondition == null) {


        /*if(locationPermissionStatus.equals("false")){
            locationView.setText("You have disabled location Services, cant access your location");
            return;
        }*/
            Log.v(LOGTAG, "Inside onConnectedz: " + String.valueOf(googleApiClient.isConnected()));
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        googleApiClient);

                if (mLastLocation != null) {
                    Log.v(LOGTAG, String.valueOf(mLastLocation.getLatitude()));
                    Log.v(LOGTAG, String.valueOf(mLastLocation.getLongitude()));
                    String message = "LATITUTE: " + String.valueOf(mLastLocation.getLatitude()) + " LONGITUDE: " + String.valueOf(mLastLocation.getLongitude());
                    locationView.setText(message);
                    //locationView.setText("Your Current location is: " + mLastLocation.toString());
                    latitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
                    longitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
                    //After getting the location of the user, the location is loaded in the Maps
                    loadMap();


                } else { //Incase there is no known last location, the request is made to get the location
                    //"adb emu geo fix 30.219470 -97.745361" use this command to put a temporary location in the emulator
                    Log.v(LOGTAG, "No last known location, location service will be called");
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                }

            } catch (SecurityException e) {
                e.printStackTrace();
                Log.v(LOGTAG, "Error in onConnected: " + e.toString());
            }
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(LOGTAG, "onLocationChanged called");
        locationView.setText("Your Current location is: "+ mLastLocation.toString());
        latitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
        longitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
        loadMap();
        loadMap();
        //publish();

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //The following loads the google maps
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(LOGTAG, "called onMapReady." );
        mMap = googleMap;
        Log.v(LOGTAG, "Longitude is: " + String.valueOf(longitude) );
        Log.v(LOGTAG, "Latitude  is: " + String.valueOf(latitude) );
        // Add a marker in the current location and move the camera
        LatLng current = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current).title("Marker in the Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 500, null);

    }
    //Call this function, when the map has to be loaded with the new location the user searches
    public void loadMap(){
        Log.v(LOGTAG, "called loadMap.");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //The following is to connect the user to the Backend server
    //Currently, MQTT is used instead of the HTTP, It will be changed in the future
    public void connect()  {
        Log.d(LOGTAG, "called mqttConnect");
        String clientId = generateClientId();
        client = new MqttAndroidClient(context, "tcp://iot.eclipse.org:1883", clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(LOGTAG, "MQTT onSuccess" + String.valueOf(onConnected));
                    mqttStatus = true;
                    subscribe();
                    if (onConnected == false) {
                        publish();
                        onConnected = true;
                    }
                    else{
                        String message = "LATITUTE: " + String.valueOf(latitude) + " LONGITUDE: " + String.valueOf(longitude);
                        locationView.setText(message);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(LOGTAG, "MQTT onFailure");
                    mqttStatus =false;



                }
            });
        } catch (MqttException e) {
            e.printStackTrace();

        }
        return;
    }
    //Subscribe is used to subscribe for the parameters from the Server. As soon as the parameters are received, the values are
    //updated in the respective views
    public void subscribe() {
        Log.d("MQTT", "called Subscribe");

        String topic = "tum/racube/audi/res";
        int qos = 1;
        try {
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(LOGTAG, "message was received");
                    //These are just dummy values received from the Server. The business logic to get the
                    //actual values will be added later. This is just for the prototype.
                   payload = message.toString();
                    temperature = 10.00;
                    // temperature = Double.parseDouble(message.toString());
                   // textView.setText("TEMPERATUE: " + message.toString());
                    //textviewPollutants.setText("POLLUTANTS: " + String.valueOf(pollutant));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOGTAG, "Sub is successful");
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //Publish is used to send the request to the server for the desired location
    public void publish() {
        Log.d(LOGTAG, "MQTT Publish");
        String topic = "tum/racube/audi/req";
        String payload = String.valueOf(latitude) + " " + String.valueOf(longitude);
        byte[] encodedPayload = new byte[0];
        try {
            Log.d(LOGTAG, "about to publish");

            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


}
