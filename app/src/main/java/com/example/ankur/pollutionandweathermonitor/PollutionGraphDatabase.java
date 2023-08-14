package com.example.ankur.pollutionandweathermonitor;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by ankur on 08.03.2016.
 */
public class PollutionGraphDatabase extends AppCompatActivity {
    protected String[] mPollutionCoordinates = new String[] {
            "AQI", "PM2.5", "PM10", "NO2", "SO2", "CO", "O3"
    };

    protected String[] mTime = new String[] {
            "0:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
            "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",
            "24:00"
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}

