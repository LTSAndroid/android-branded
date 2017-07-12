package com.pixelmags.android.IssueView;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.util.IssueViewUtils;

import io.fabric.sdk.android.Fabric;

public class IssueViewActivity extends Activity{

	private IssueViewUtils utils;
	private IssueViewAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_fullscreen);

		viewPager = (ViewPager) findViewById(R.id.pager);

		utils = new IssueViewUtils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);

	/*	adapter = new IssueViewAdapter(IssueViewActivity.this,
				utils.getFilePaths());

		viewPager.setAdapter(adapter);*/

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
}
