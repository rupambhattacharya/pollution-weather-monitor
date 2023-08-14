package com.example.rupam.pollutionandweathermonitor;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by Rupam on 3/8/2016.
 */
public class GIFWebView extends WebView {

    public GIFWebView(Context context, String path) {
        super(context);

        loadUrl(path);
    }
}
