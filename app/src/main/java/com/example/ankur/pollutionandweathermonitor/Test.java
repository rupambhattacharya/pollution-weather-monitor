package com.example.ankur.pollutionandweathermonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class Test extends PollutionGraphDatabase implements
        OnChartValueSelectedListener {
    public final static String EXTRA_MESSAGE_LONGITUTE = "longitude";
    public final static String EXTRA_MESSAGE_LATITUTE = "latitude";
    public final static String EXTRA_MESSAGE_CONDITION = "condition";
    public static final String EXTRA_MESSAGE_PAYLOAD ="temperature";
    public static final String EXTRA_MESSAGE_POLLUTANT ="pollutant";


    private String onReceived;
    private static String payload;
    private double longitude;
    private double latitude;
    protected HorizontalBarChart mChart;
    private TextView tvX, tvY;
    private LoginButton loginButton;
    private String[] pollutant;
    private Context context;
    private float value;
    private boolean conditionAlert = true;

    private Typeface tf;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTextSize(15);
        Intent intent = getIntent();
        onReceived = intent.getStringExtra(Weather.EXTRA_MESSAGE_CONDITION);

        if(onReceived != null){
            longitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(Weather.EXTRA_MESSAGE_PAYLOAD);
            Log.d("Value", String.valueOf(conditionAlert));
            //Log.d("Pollution", payload);

        }
        onReceived = intent.getStringExtra(HeatMap.EXTRA_MESSAGE_CONDITION);
        if(onReceived != null){
            longitude = intent.getExtras().getDouble(HeatMap.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(HeatMap.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(HeatMap.EXTRA_MESSAGE_PAYLOAD);
            Log.d("Value", String.valueOf(conditionAlert));


            //Log.d("Pollution", payload);

        }
        onReceived = intent.getStringExtra(PollutionO3.EXTRA_MESSAGE_CONDITION);

        if(onReceived != null){
            Log.d("Pollution", "onReceived is null");
            longitude = intent.getExtras().getDouble(PollutionO3.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(PollutionO3.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(PollutionO3.EXTRA_MESSAGE_PAYLOAD);
            conditionAlert = false;
            Log.d("Value", String.valueOf(conditionAlert));

            Log.d("Value", "called by PollutantO3");
            //Log.d("Pollution", payload);

        }
        if(onReceived == null){
            Log.d("Pollution", "onReceived is null");
            longitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(DisplayInformation.EXTRA_MESSAGE_PAYLOAD);
            Log.d("Value", String.valueOf(conditionAlert));

            //Log.d("Pollution", payload);

        }


        mChart = (HorizontalBarChart) findViewById(R.id.chart1);
        mChart.setDrawBarShadow(false);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
        yl.setAxisMinValue(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinValue(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        setData(7, 50);
        mChart.animateY(2500);
        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        // mChart.setDrawLegend(false);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<IBarDataSet> sets = mChart.getData()
                        .getDataSets();

                for (IBarDataSet iSet : sets) {

                    IBarDataSet set = (BarDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
        }
        return true;
    }


    private void setData(int count, float range) {
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        if(payload != null){
            Log.d("Pollutant_Setdata", payload);
            pollutant = payload.split(",");
            for (int i = 0; i < count; i++) {
                xVals.add(mPollutionCoordinates[i % 7]);
                yVals.add(new BarEntry((float)(Double.parseDouble(pollutant[i])), i));
            }

        }
        else{
            Log.d("Pollutant_Setdata", "NULL values");
            for (int i = 0; i < count; i++) {
                xVals.add(mPollutionCoordinates[i % 7]);
                value = (float)(Math.random() * range);
                yVals.add(new BarEntry(value, i));

            }
            if(conditionAlert == true) {
                Log.d("Value", String.valueOf(value));
                Alertbox(value);

            }
        }





        MyBarDataset set = new MyBarDataset(yVals, "");

        set.setColors(new int[]{Color.GREEN, Color.YELLOW, Color.RED});
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }



    public void RedirectToHome(View view){

        Intent intent = new Intent(this, DisplayInformation.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        startActivity(intent);
    }
    public void RedirectToWeather(View view){
        Intent intent = new Intent(this, Weather.class);
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
    public void RedirectToPollutionO3(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "O3 POLLUTION LEVEL");

        startActivity(intent);
    }
    public void RedirectToPollutionCO(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "CO POLLUTION LEVEL");

        startActivity(intent);
    }
    public void RedirectToPollutionSO2(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "SO2 POLLUTION LEVEL");

        startActivity(intent);
    }
    public void RedirectToPollutionNO2(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "NO2 POLLUTION LEVEL");

        startActivity(intent);
    }
    public void RedirectToPollutionPM10(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "PM 10 POLLUTION LEVEL");

        startActivity(intent);
    }
    public void RedirectToPollutionPM25(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "PM 2.5 POLLUTION LEVEL");

        startActivity(intent);
    }
    public void RedirectToPollutionAQI(View view){
        Intent intent = new Intent(this, PollutionO3.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        intent.putExtra(EXTRA_MESSAGE_POLLUTANT, "AQI POLLUTION LEVEL");

        startActivity(intent);
    }
    public void Alertbox(float value){
        String message;
        if(value <10) {
            message = "Air is Fresh outside. Excellent time to have a run outside.";
            Log.d("Value", message);
            Log.d("Value", String.valueOf(value));
        }
        else if(value>10 && value<20) {
            message = "Air Quality is moderate";
            Log.d("Value", message);
            Log.d("Value", String.valueOf(value));
        }
        else if(value>20 && value<30){
            message = "Air Quality is bad. Advisable to stay indoors. If leaving, take a mask";
            Log.d("Value", message);
            Log.d("Value", String.valueOf(value));
        }
        else {
            message = "Air Quality is beyond the recommended Limits. We advice not to go out";
            Log.d("Value", message);
            Log.d("Value", String.valueOf(value));
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
