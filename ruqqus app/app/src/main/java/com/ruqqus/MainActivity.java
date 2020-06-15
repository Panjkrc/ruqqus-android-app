package com.ruqqus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.INVISIBLE;


public class MainActivity extends Activity {
    RotateAnimation rotate = new RotateAnimation(
            0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );
    private WebView mWebview;
    private ProgressBar progressBar;
    private ImageView logo;
    private TextView errorOutputTextView;


    String myurl = "https://ruqqus.com";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent fromExternalintent = getIntent();

        if (fromExternalintent != null && fromExternalintent.hasCategory("android.intent.category.BROWSABLE")) {

            myurl = fromExternalintent.getDataString();

        }

        setContentView(R.layout.activity_main);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }



        errorOutputTextView = findViewById(R.id.errorOutput);
        logo = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        mWebview = findViewById(R.id.webView);

        mWebview.setVisibility(INVISIBLE);

        double logo_rotation_speed = 1.0;
        rotate.setDuration(((long) (1000 / logo_rotation_speed)));
        rotate.setRepeatCount(Animation.INFINITE);
        logo.startAnimation(rotate);


        mWebview.getSettings().setDatabaseEnabled(true);
        mWebview.getSettings().setGeolocationEnabled(true);
        mWebview.getSettings().setSupportMultipleWindows(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setJavaScriptEnabled(true);



        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ("ruqqus.com".equals(Uri.parse(url).getHost())) {
                    // This is my website, so do not override; let my WebView load the page
                    return false;
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                Intent intent = new Intent(getBaseContext(), browserActivity.class);
                intent.putExtra("EXTERNAL_URL", url);
                startActivity(intent);

                return true;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebview.stopLoading();
                mWebview.setVisibility(INVISIBLE);

                Intent intent = new Intent(getBaseContext(), errorHandler.class);
                startActivity(intent);

                errorOutputTextView.setVisibility(View.VISIBLE);
                errorOutputTextView.setText("\nError code: " + errorCode + "\nError description: " + description + "\nFailingURL: " + failingUrl);

                Toast.makeText(getApplicationContext(), "Error occured, please check newtwork connectivity", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebview.setVisibility(View.VISIBLE);
                rotate.cancel();
                logo.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.GONE);


            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {



            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);

                }

                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);

                }
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebview.loadUrl(myurl);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
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