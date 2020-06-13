package com.ruqqus;

import androidx.appcompat.app.AppCompatActivity;

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
    private WebView mWebView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        progressBar2 = findViewById(R.id.progressBar2);
        textView = (TextView) findViewById(R.id.textView);
        mWebView = (WebView) findViewById(R.id.webViewBrowser);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url) {
                textView.setText(mWebView.getTitle());
            }
        });
        mWebView.getSettings().getJavaScriptEnabled();
        String ExternalUrl = getIntent().getStringExtra("EXTERNAL_URL");
        mWebView.setWebChromeClient(new WebChromeClient(){



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

        mWebView.loadUrl(ExternalUrl);

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
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
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(mWebView.canGoBack() == true){
                        mWebView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}