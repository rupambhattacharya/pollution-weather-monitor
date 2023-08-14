package com.example.ankur.pollutionandweathermonitor;

/**
 * Created by ankur on 08.03.2016.
 */
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by Rupam on 3/7/2016.
 */
public class MyBarDataset extends BarDataSet {
    public MyBarDataset(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForXIndex(index).getVal() < 15) // less than 15 green
            return mColors.get(0);
        else if(getEntryForXIndex(index).getVal() < 30) // less than 30 yellow
            return mColors.get(1);
        else // greater or equal than 100 red
            return mColors.get(2);
    }
}


