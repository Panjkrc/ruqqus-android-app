package com.ruqqus;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity {
    private WebView mWebview ;
    ProgressBar progressBar;

    String myurl = "https://ruqqus.com";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        progressBar = findViewById(R.id.progressBar);
        mWebview = (WebView)findViewById(R.id.webView);
        mWebview.getSettings().setJavaScriptEnabled(true);


        mWebview.setWebViewClient(new WebViewClient(){

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

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(getApplicationContext(), "Cannot load page", Toast.LENGTH_LONG).show();
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient(){




            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                if(newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE){
                    progressBar.setVisibility(ProgressBar.VISIBLE);

                }

                progressBar.setProgress(newProgress);
                if(newProgress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);

                }
            }
        });
        mWebview.loadUrl(myurl);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(mWebview.canGoBack() == true){
                        mWebview.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}