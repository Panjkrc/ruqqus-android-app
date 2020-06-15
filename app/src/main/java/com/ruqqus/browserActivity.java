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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

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
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                textView.setMaxLines(1);
                textView.setText(mWebview.getUrl());
            }
        });

        mWebview.getSettings().setDatabaseEnabled(true);
        mWebview.getSettings().setGeolocationEnabled(true);
        mWebview.getSettings().setSupportMultipleWindows(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setJavaScriptEnabled(true);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String ExternalUrl = getIntent().getStringExtra("EXTERNAL_URL");
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
        inflater.inflate(R.menu.menu, menu);
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

                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("simple text", mWebview.getUrl());
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