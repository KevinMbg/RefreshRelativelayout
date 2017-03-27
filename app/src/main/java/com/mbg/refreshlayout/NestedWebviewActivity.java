package com.mbg.refreshlayout;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

public class NestedWebviewActivity extends BaseActivity implements IRefreshListener {

    private RefreshRelativeLayout refreshRelativeLayout;
    private WebView mWebView ;
    private boolean refreshIsPositive=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_webview);
        initView();
    }

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.addRefreshListener(this);
        mWebView =(WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(refreshIsPositive){
                    refreshRelativeLayout.positiveRefreshComplete();
                }else{
                    refreshRelativeLayout.negativeRefreshComplete();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if(!refreshIsPositive) {
                    refreshRelativeLayout.negativeRefreshComplete();
                }
            }
        });
        refreshRelativeLayout.startPositiveRefresh();
    }

    @Override
    protected String getActivityTitle() {
        return "嵌套WebView";
    }

    @Override
    public void onPositiveRefresh() {
        refreshIsPositive=true;
        mWebView.loadUrl("http://m.thepaper.cn/");
    }

    @Override
    public void onNegativeRefresh() {
        refreshIsPositive=false;
        //mWebView.loadUrl("http://www.jianshu.com/p/7cbbc4f009d1");
        mWebView.loadUrl("javascript:loadnextpage()");
    }
}
