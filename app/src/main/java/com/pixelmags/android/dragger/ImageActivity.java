/*
* Copyright (C) 2015 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.pixelmags.android.dragger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.github.ppamorim.dragger.DraggerPosition;
import com.github.ppamorim.dragger.DraggerView;
import com.pixelmags.android.pixelmagsapp.R;


public class ImageActivity extends AbstractActivity {

  private static final String CAN_ANIMATE = "can_animate";
  public static final String DRAG_POSITION = "drag_position";

  WebView web;
  ProgressDialog dialog;

  //@InjectView(R.id.toolbar) Toolbar toolbar;
  //@InjectView(com.github.ppamorim.dragger.R.id.dragger_view)
  Toolbar toolbar;
  DraggerView draggerView;

  @Override protected int getContentViewId() {
    return R.layout.activity_dragger;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    toolbar =(Toolbar)findViewById(R.id.toolbar);
    draggerView =(DraggerView)findViewById(R.id.dragger_view);
    dialog = new ProgressDialog(this);
    web = (WebView) findViewById(R.id.webdata);
    dialog = ProgressDialog.show(ImageActivity.this, null,
            "Please Wait...Page is Loading...");
    dialog.setCancelable(true);


    Log.e("DATA",String.valueOf(getIntent().getStringExtra("WebPageUrl")).replaceAll("click for more info : ",""));


    configToolbar();
    configIntents();

    web.setWebViewClient(new WebViewClient() {

      // This method will be triggered when the Page Started Loading

      public void onPageStarted(WebView view, String url, Bitmap favicon) {

        dialog.show();
        super.onPageStarted(view, url, favicon);
      }

      // This method will be triggered when the Page loading is completed

      public void onPageFinished(WebView view, String url) {
        dialog.dismiss();
        super.onPageFinished(view, url);
      }

      // This method will be triggered when error page appear

      public void onReceivedError(WebView view, int errorCode,
                                  String description, String failingUrl) {
        dialog.dismiss();
        // You can redirect to your own page instead getting the default
        // error page
        Toast.makeText(ImageActivity.this,
                "The Requested Page Does Not Exist", Toast.LENGTH_LONG).show();
        web.loadUrl(String.valueOf(getIntent().getStringExtra("WebPageUrl")));
        super.onReceivedError(view, errorCode, description, failingUrl);
      }
    });
    web.loadUrl(String.valueOf(String.valueOf(getIntent().getStringExtra("WebPageUrl")).replaceAll("click for more info : ","")));
    web.getSettings().setLoadWithOverviewMode(true);
    web.getSettings().setUseWideViewPort(true);
  }




  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onBackPressed() {
    draggerView.closeActivity();
  }

  private void configToolbar() {
    setSupportActionBar(toolbar);
    toolbar.setTitle(getResources().getString(R.string.app_name));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private void configIntents() {
    draggerView.setDraggerPosition((DraggerPosition) getIntent().getSerializableExtra(DRAG_POSITION));
  }

}