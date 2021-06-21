package com.example.myapplication;


import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Oje extends AppCompatActivity {
    Button on_btn;
    int toggle = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oje);

        on_btn = (Button) findViewById(R.id.on_btn);
        on_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle == 0) {
                    WebView webView = (WebView) findViewById(R.id.webView);
                    webView.setWebViewClient(new WebViewClient());
                    webView.setBackgroundColor(255);

                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(false);

                    webView.loadUrl("");

                    on_btn.setText("OFF");
                    toggle = 1;
                } else if (toggle == 1) {
                    WebView webView = (WebView) findViewById(R.id.webView);
                    webView.setWebViewClient(new WebViewClient());
                    webView.setBackgroundColor(255);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setUseWideViewPort(true);
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                    webView.loadUrl("http://192.168.137.169:8000/index.html");

                    on_btn.setText("ON");
                    toggle = 0;
                }
            }
        });

    }
}
