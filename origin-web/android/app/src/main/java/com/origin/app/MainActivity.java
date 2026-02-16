package com.origin.app;

import com.getcapacitor.BridgeActivity;
import android.webkit.WebView;
import android.os.Bundle;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        WebView webView = (WebView) this.bridge.getWebView();
        // 核心代码：禁止 WebView 自动缩放页面以适应屏幕
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);
    }
}
