package com.ruqqus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class browserActivity extends AppCompatActivity {


    ProgressBar progressBar2;
    private WebView mWebview;
    private TextView textView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        progressBar2 = findViewById(R.id.progressBar2);
        textView = findViewById(R.id.textView);
        mWebview = findViewById(R.id.webViewBrowser);
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url) {
                textView.setText(mWebview.getTitle());
            }
        });

        mWebview.getSettings().getJavaScriptEnabled();
        mWebview.getSettings().setDatabaseEnabled(true);
        mWebview.getSettings().setGeolocationEnabled(true);
        mWebview.getSettings().setSupportMultipleWindows(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setJavaScriptEnabled(true);

        String ExternalUrl = getIntent().getStringExtra("EXTERNAL_URL");
        mWebview.setWebChromeClient(new WebChromeClient(){



            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar2.setVisibility(View.VISIBLE);
                if(newProgress < 100 && progressBar2.getVisibility() == ProgressBar.GONE){
                    progressBar2.setVisibility(ProgressBar.VISIBLE);

                }

                progressBar2.setProgress(newProgress);
                if(newProgress == 100) {
                    progressBar2.setVisibility(ProgressBar.GONE);

                }
            }
        });

        mWebview.loadUrl(ExternalUrl);

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}