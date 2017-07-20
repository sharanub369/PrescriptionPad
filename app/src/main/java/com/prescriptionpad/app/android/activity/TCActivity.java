package com.prescriptionpad.app.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.DialogHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sharana.b on 5/12/2017.
 */

public class TCActivity extends AppCompatActivity {

    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;
    private DialogHandler dialogHandler;

    @InjectView(R.id.webViewId)
    public WebView faqWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_faq);
        ButterKnife.inject(this);
        setUpToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUrl();
    }

    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText(Constants.PageTitles.KEY_TC_PAGE);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private ActionBar getActionbarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }

    public void loadUrl() {
        dialogHandler = new DialogHandler(this);
        dialogHandler.displayDialog("");
        faqWebView.getSettings().setJavaScriptEnabled(true);
        faqWebView.getSettings().setDomStorageEnabled(true);
        faqWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        faqWebView.getSettings().setBuiltInZoomControls(false);
        faqWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        faqWebView.getSettings().setSupportMultipleWindows(false);
        faqWebView.getSettings().setUseWideViewPort(true);
        faqWebView.getSettings().setLoadWithOverviewMode(true);
        faqWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (dialogHandler != null) {
                    dialogHandler.dismissDialog();
                }
            }

        });
        faqWebView.setWebChromeClient(new WebChromeClient());
        faqWebView.loadUrl(Constants.KEY_TC_URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
