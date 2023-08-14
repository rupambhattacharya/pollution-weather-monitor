package com.example.rupam.pollutionandweathermonitor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class Pollution extends PollutionGraphDatabase implements
        OnChartValueSelectedListener {
    private LoginButton loginButton;
    public final static String EXTRA_MESSAGE_CONDITION = "condition";
    public static final String EXTRA_MESSAGE_LONGITUTE ="longitude";
    public static final String EXTRA_MESSAGE_LATITUTE ="latitude";
    public static final String EXTRA_MESSAGE_PAYLOAD ="temperature";
    private String onReceived;
    private static String payload;
    private double longitude;
    private double latitude;
    private String[] pollution;
    private int[] pollutionValues = new int[5];

    protected HorizontalBarChart mChart;
    private TextView tvX, tvY;

    private Typeface tf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pollution);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        mChart = (HorizontalBarChart) findViewById(R.id.chart1);
        mChart.setDrawBarShadow(false);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
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
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTextSize(15);
        Intent intent = getIntent();
        onReceived = intent.getStringExtra(Weather.EXTRA_MESSAGE_CONDITION);

        if(onReceived != null){
            longitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(Weather.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(Weather.EXTRA_MESSAGE_PAYLOAD);
            //Log.d("Pollution", payload);

        }
        onReceived = intent.getStringExtra(HeatMap.EXTRA_MESSAGE_CONDITION);
        if(onReceived != null){
            longitude = intent.getExtras().getDouble(HeatMap.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(HeatMap.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(HeatMap.EXTRA_MESSAGE_PAYLOAD);
            //Log.d("Pollution", payload);

        }
        if(onReceived == null){
            Log.d("Pollution", "onReceived is null");
            longitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(DisplayInformation.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(DisplayInformation.EXTRA_MESSAGE_PAYLOAD);
            //Log.d("Pollution", payload);

        }
        if(payload != null) {
            pollution = payload.split(",");

            pollutionValues[0] = Integer.parseInt(pollution[0]);
            pollutionValues[1] = Integer.parseInt(pollution[1]);
            pollutionValues[2] = Integer.parseInt(pollution[2]);
            pollutionValues[3] = Integer.parseInt(pollution[3]);
            pollutionValues[4] = Integer.parseInt(pollution[4]);
            Log.d("Pollution", pollution[0]);
            Log.d("Pollution", pollution[1]);
            Log.d("Pollution", pollution[2]);
            Log.d("Pollution", pollution[3]);
            Log.d("Pollution", pollution[4]);
        }
        else{
            //Incase the Server is down, show graph with dummy values
            pollutionValues[0] = 352;
            pollutionValues[1] = 52;
            pollutionValues[2] = 552;
            pollutionValues[3] = 72;
            pollutionValues[4] = 97;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void RedirectToHome(View view){
        //Log.d("Weather", String.valueOf(longitude));
        //Log.d("Weather", String.valueOf(latitude));
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
    Log.d("Function", "Setdara");
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            xVals.add(mPollutionCoordinates[i % 7]);
            yVals.add(new BarEntry((float) (Math.random() * range), i));
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

}
