package com.mega.ebook;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    WebView webView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    //@SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        webView = findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.addJavascriptInterface(this, "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            System.out.println("web setting!");
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } else {
            //webView.crossdomain();
        }

        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put(" Access-Control-Allow-Origin","*");
        webView.setWebViewClient(getWebViewClientWithCustomHeader());
        //webView.loadUrl("http://ondebt.ru/a.php", extraHeaders);
        webView.loadUrl("https://bilimland.kz/kk/process/access/authorize");
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //webView.setWebViewClient(null);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://bilimland.kz/");
        //webView.loadUrl("https://bilimland.kz/upload/content/lesson/10083/media/8c0e21f0c4029e6ca2289c0f9f5920da/index.html");
    }

    public WebViewClient getWebViewClientWithCustomHeader(){
        return new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("login", "daryn.169")
                            .add("password", "76717")
                            .add("remember", "on")
                            .build();
                    OkHttpClient httpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url.trim())
                            .post(requestBody)
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("Cookie", "PHPSESSID=ps4f796fkf954qsv8791e7fi63; lang=kk; _ga=GA1.2.406234785.1515770541; _gid=GA1.2.930180801.1515770541; session=ce6f3ea3a83a88dbd7f32e6d062350cc.1785797; userId=382476\n")
                            .build();
                    Response response = httpClient.newCall(request).execute();

                    return new WebResourceResponse(
                            response.header("content-type", response.body().contentType().type()), // You can set something other as default content-type
                            response.header("content-encoding", "utf-8"),  // Again, you can set another encoding as default
                            response.body().byteStream()
                    );
                } catch (IOException e) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    return null;
                }
            }
        };

    }


    public WebViewClient getWebViewClientHeader(){
        return new WebViewClient() {
            //@Override
            public WebResourceRequest shouldInterceptRequest(WebView view, String url){
                return new WebResourceRequest(
                            /*response.header("content-type", response.body().contentType().type()), // You can set something other as default content-type
                            response.header("content-encoding", "utf-8"),  // Again, you can set another encoding as default
                            response.body().byteStream()*/
                ) {

                    public Uri getUrl() {
                        return null;
                    }

                    @Override
                    public boolean isForMainFrame() {
                        return false;
                    }

                    //@Override
                    public boolean isRedirect() {
                        return false;
                    }

                    //@Override
                    public boolean hasGesture() {
                        return false;
                    }

                    //@Override
                    public String getMethod() {
                        return null;
                    }

                    //@Override
                    public Map<String, String> getRequestHeaders() {
                        return null;
                    }
                };
            }

            @Override
            public WebResourceResponse shouldInterceptResponse(WebView view, String url) {
                try {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("login", "daryn.169")
                            .add("password", "76717")
                            .add("remember", "on")
                            .build();
                    OkHttpClient httpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url.trim())
                            .get()
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("Cookie", "PHPSESSID=ps4f796fkf954qsv8791e7fi63; lang=kk; _ga=GA1.2.406234785.1515770541; _gid=GA1.2.930180801.1515770541; session=ce6f3ea3a83a88dbd7f32e6d062350cc.1785797; userId=382476\n")
                            .build();
                    Response response = httpClient.newCall(request).execute();

                    return new WebResourceResponse(
                            response.header("content-type", response.body().contentType().type()), // You can set something other as default content-type
                            response.header("content-encoding", "utf-8"),  // Again, you can set another encoding as default
                            response.body().byteStream()
                    );
                } catch (IOException e) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    return null;
                }
            }
        };

    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
