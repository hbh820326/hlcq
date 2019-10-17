package com.exchange_rate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

/**
 * Created by 林 on 2017/1/8.
 */

public class BrowserActivity extends Activity {
    public static final String PACKAGE_URL = "http://gdown.baidu.com/data/wisegame/df65a597122796a4/weixin_821.apk";

    ProgressBar progressBar;
    private long breakPoints;
    private File file;
    private long totalBytes;
    private long contentLength;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);
        WebView webView= (WebView) findViewById(R.id.web_content);
        WebSettings webSettings = webView.getSettings();// 获取WebSetting对象
        webSettings.setBuiltInZoomControls(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.loadUrl(getIntent().getStringExtra("url"));
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//在当前view中打开超链接
                return true;
            }

        });

    }

//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.downloadButton:
//                // 新下载前清空断点信息
//                breakPoints = 0L;
//                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample.apk");
//                downloader = new ProgressDownloader(PACKAGE_URL, file, this);
//                downloader.download(0L);
//                break;
//            case R.id.pause_button:
//                downloader.pause();
//                Toast.makeText(this, "下载暂停", Toast.LENGTH_SHORT).show();
//                // 存储此时的totalBytes，即断点位置。
//                breakPoints = totalBytes;
//                break;
//            case R.id.continue_button:
//                downloader.download(breakPoints);
//                break;
//        }
//    }
//
//    @Override
//    public void onPreExecute(long contentLength) {
//        // 文件总长只需记录一次，要注意断点续传后的contentLength只是剩余部分的长度
//        if (this.contentLength == 0L) {
//            this.contentLength = contentLength;
//            progressBar.setMax((int) (contentLength / 1024));
//        }
//    }
//
//    @Override
//    public void update(long totalBytes, boolean done) {
//        // 注意加上断点的长度
//        this.totalBytes = totalBytes + breakPoints;
//        progressBar.setProgress((int) (totalBytes + breakPoints) / 1024);
//        if (done) {
//            Toast.makeText(BrowserActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
//        }
//    }
//}

}
