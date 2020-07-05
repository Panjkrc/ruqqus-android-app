package com.ruqqus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.view.View.INVISIBLE;

public class MainActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_LOLIPOP = 1;
    private final static int RESULT_CODE_ICE_CREAM = 2;
    private static ValueCallback<Uri[]> mFilePathCallback;
    private static String mCameraPhotoPath;
    private static ValueCallback<Uri> mUploadMessage;

    RotateAnimation rotate = new RotateAnimation(
            0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );
    String myurl = "https://ruqqus.com";
    String[] supported_urls = {
            "ruqqus.com",
            "i.ruqqus.com"
    };

    private WebView mWebview;
    private ProgressBar progressBar;
    private ImageView logo;
    private DrawerLayout drawerLayout;

    public static File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @SuppressLint({"SetJavaScriptEnabled", "SdCardPath"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url_from_browser_activity = getIntent().getStringExtra("RUQQUS_URL");

        if (url_from_browser_activity != null) {
            myurl = url_from_browser_activity;

        }

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

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }


        logo = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        mWebview = findViewById(R.id.webView);


        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCachePath(getCacheDir().getPath());
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebview.getSettings().setAllowFileAccess(true);
        mWebview.getSettings().setAllowContentAccess(true);
        mWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebview.getSettings().setDatabaseEnabled(true);
        mWebview.getSettings().setGeolocationEnabled(true);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.getSettings().setDisplayZoomControls(false);

        registerForContextMenu(mWebview);

        StartLoadingScreen();

        mWebview.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url == null) {
                    return false;
                }
                if (url.startsWith("market://")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if (url.startsWith("mailto:")) {

                    try {
                        List<String> to = new ArrayList<>();
                        List<String> cc = new ArrayList<>();
                        List<String> bcc = new ArrayList<>();
                        String subject = null;
                        String body = null;

                        url = url.replaceFirst("mailto:", "");

                        String[] urlSections = url.split("&");
                        if (urlSections.length >= 2) {

                            to.addAll(Arrays.asList(urlSections[0].split(",")));

                            for (int i = 1; i < urlSections.length; i++) {
                                String urlSection = urlSections[i];
                                String[] keyValue = urlSection.split("=");

                                if (keyValue.length == 2) {
                                    String key = keyValue[0];
                                    String value = keyValue[1];

                                    value = URLDecoder.decode(value, "UTF-8");

                                    switch (key) {
                                        case "cc":
                                            cc.addAll(Arrays.asList(url.split(",")));
                                            break;
                                        case "bcc":
                                            bcc.addAll(Arrays.asList(url.split(",")));
                                            break;
                                        case "subject":
                                            subject = value;
                                            break;
                                        case "body":
                                            body = value;
                                            break;
                                    }
                                }
                            }
                        } else {
                            to.addAll(Arrays.asList(url.split(",")));
                        }

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");

                        String[] dummyStringArray = new String[0]; // For list to array conversion
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, to.toArray(dummyStringArray));
                        if (cc.size() > 0) {
                            emailIntent.putExtra(Intent.EXTRA_CC, cc.toArray(dummyStringArray));
                        }
                        if (bcc.size() > 0) {
                            emailIntent.putExtra(Intent.EXTRA_BCC, bcc.toArray(dummyStringArray));
                        }
                        if (subject != null) {
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        }
                        if (body != null) {
                            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                        }
                        view.getContext().startActivity(emailIntent);

                        return true;
                    } catch (UnsupportedEncodingException e) {
                        /* Won't happen*/
                    }

                }


                for (String inArray : supported_urls) {
                    // This is my website, so do not override; let my WebView load the page
                    if (inArray.equals(Uri.parse(url).getHost())) {
                        return false;
                    }

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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                EndLoadingScreen();

            }
        });


        mWebview.setWebChromeClient(new WebChromeClient() {

            String TAG;

            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, REQUEST_CODE_LOLIPOP);


                return true;
            }


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
                if (newProgress >= 80) {
                    EndLoadingScreen();
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

    void StartLoadingScreen() {
        mWebview.setVisibility(INVISIBLE);
        double logo_rotation_speed = 1.0;
        rotate.setDuration(((long) (1000 / logo_rotation_speed)));
        rotate.setRepeatCount(Animation.INFINITE);
        logo.startAnimation(rotate);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    void EndLoadingScreen() {
        mWebview.setVisibility(View.VISIBLE);
        rotate.cancel();
        logo.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CODE_ICE_CREAM:
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                mUploadMessage.onReceiveValue(uri);
                mUploadMessage = null;
                break;
            case REQUEST_CODE_LOLIPOP:
                Uri[] results = null;
                // Check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        // If there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.drawer_home:
                mWebview.loadUrl(myurl);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            case R.id.drawer_create_post:
                mWebview.loadUrl("https://ruqqus.com/submit");
                drawer.closeDrawer(GravityCompat.START);

                return true;
            case R.id.drawer_settings_account:
                mWebview.loadUrl("https://ruqqus.com/settings");
                drawer.closeDrawer(GravityCompat.START);

                return true;
            case R.id.drawer_about:
                mWebview.loadUrl("https://ruqqus.com/help/about");
                drawer.closeDrawer(GravityCompat.START);

                return true;

            default:
                return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final WebView.HitTestResult webViewHitTestResult = mWebview.getHitTestResult();

        if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            contextMenu.setHeaderTitle("Download Image From Below");

            contextMenu.add(0, 1, 0, "Save - Download Image")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            String DownloadImageURL = webViewHitTestResult.getExtra();

                            if (URLUtil.isValidUrl(DownloadImageURL)) {

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                                request.allowScanningByMediaScanner();


                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);

                                Toast.makeText(MainActivity.this, "Image Downloaded Successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Sorry.. Something Went Wrong.", Toast.LENGTH_LONG).show();
                            }
                            return false;
                        }
                    });
            contextMenu.add(0, 2, 1, "Share post")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            String DownloadImageURL = webViewHitTestResult.getExtra();

                            Intent myIntent = new Intent(Intent.ACTION_SEND);
                            myIntent.setType("text/plain");
                            myIntent.putExtra(Intent.EXTRA_TEXT, DownloadImageURL);
                            startActivity(Intent.createChooser(myIntent, "Share Using"));
                            return false;
                        }
                    });
        }
    }
}
