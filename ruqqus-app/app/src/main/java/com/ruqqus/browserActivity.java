package com.ruqqus;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import static android.view.View.INVISIBLE;

public class browserActivity extends AppCompatActivity {


    ProgressBar progressBar2;
    Toolbar toolbar;
    private WebView mWebview;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        progressBar2 = findViewById(R.id.progressBar2);


        mWebview = findViewById(R.id.webViewBrowser);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCachePath(getCacheDir().getPath());
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebview.getSettings().setAllowFileAccess(true);
        mWebview.getSettings().setAllowContentAccess(true);
        mWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebview.getSettings().setDatabaseEnabled(true);
        mWebview.getSettings().setGeolocationEnabled(true);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.getSettings().setDisplayZoomControls(false);

        String ExternalUrl = getIntent().getStringExtra("EXTERNAL_URL");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                String[] error_data = {
                        String.valueOf(errorCode),
                        description,
                        failingUrl
                };

                mWebview.stopLoading();
                mWebview.setVisibility(INVISIBLE);

                Intent intent = new Intent(getBaseContext(), errorHandlerActivity.class);
                intent.putExtra("ERROR_DATA", error_data);
                startActivity(intent);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ("ruqqus.com".equals(Uri.parse(url).getHost())) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("URL_FROM_BROWSER_ACTIVITY", url);
                    startActivity(intent);
                    finish();
                    return true;
                } else
                    return false;
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar2.setVisibility(View.VISIBLE);
                if (newProgress < 100 && progressBar2.getVisibility() == ProgressBar.GONE) {
                    progressBar2.setVisibility(ProgressBar.VISIBLE);

                }

                progressBar2.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar2.setVisibility(ProgressBar.GONE);

                }
            }
        });

        mWebview.loadUrl(ExternalUrl);
        Objects.requireNonNull(getSupportActionBar()).setTitle(mWebview.getUrl());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browser_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                mWebview.loadUrl(mWebview.getUrl());
                return true;

            case R.id.cancel:
                mWebview.stopLoading();
                return true;

            case R.id.copy_url:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url from ruqqus", mWebview.getUrl());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_LONG).show();
                return true;

            case R.id.open_in_external_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebview.getUrl()));
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
