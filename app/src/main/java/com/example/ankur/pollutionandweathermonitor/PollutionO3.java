package com.example.ankur.pollutionandweathermonitor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class PollutionO3 extends AppCompatActivity implements
        OnChartValueSelectedListener, OnChartGestureListener {
    public final static String EXTRA_MESSAGE_LONGITUTE = "longitude";
    public final static String EXTRA_MESSAGE_LATITUTE = "latitude";
    public final static String EXTRA_MESSAGE_CONDITION = "condition";
    public static final String EXTRA_MESSAGE_PAYLOAD ="temperature";
    private String onReceived;
    private static String payload;
    private double longitude;
    private double latitude;
    protected HorizontalBarChart mChart;
    private TextView tvX, tvY;
    private LoginButton loginButton;
    private String[] pollutant;
    private LineChart mChart1;
    private String heading;
    private Typeface tf;
    private TextView textView;
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
        setContentView(R.layout.activity_pollution_o3);
        textView = (TextView) findViewById(R.id.textView1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTextSize(15);
        Intent intent = getIntent();
        onReceived = intent.getStringExtra(Test.EXTRA_MESSAGE_CONDITION);

        if(onReceived != null){
            longitude = intent.getExtras().getDouble(Test.EXTRA_MESSAGE_LONGITUTE);
            latitude = intent.getExtras().getDouble(Test.EXTRA_MESSAGE_LATITUTE);
            payload = intent.getStringExtra(Test.EXTRA_MESSAGE_PAYLOAD);
            heading = intent.getStringExtra(Test.EXTRA_MESSAGE_POLLUTANT);

            //Log.d("Pollution", payload);

        }
    if(heading != null){
        textView.setText(heading);
    }
//Line Chart for O3
        mChart1 = (LineChart) findViewById(R.id.chart2);
        mChart1.setOnChartGestureListener(this);
        mChart1.setOnChartValueSelectedListener(this);
        mChart1.setDrawGridBackground(false);

        mChart1.setDescription("");

        mChart1.setTouchEnabled(true);
        mChart1.setDragEnabled(true);
        mChart1.setScaleEnabled(true);
        mChart1.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mChart1.setMarkerView(mv);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart1.getXAxis();

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(130f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);
/*
        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);
*/
        YAxis leftAxis = mChart1.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
  //      leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaxValue(220f);
        leftAxis.setAxisMinValue(-50f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart1.getAxisRight().setEnabled(false);

        // Setting Data for Line Chart for O3
        setData_O3(45, 100);
    }

    private void setData_O3(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Monthly Pollution Levels");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart1.setData(data);
    }
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart1.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }
    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndeax, Highlight h) {
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
    public void RedirectToPollution(View view){
        Intent intent = new Intent(this, Test.class);
        intent.putExtra(EXTRA_MESSAGE_LATITUTE, latitude);
        intent.putExtra(EXTRA_MESSAGE_LONGITUTE, longitude);
        intent.putExtra(EXTRA_MESSAGE_CONDITION, "true");
        intent.putExtra(EXTRA_MESSAGE_PAYLOAD, payload);
        startActivity(intent);
    }

}
