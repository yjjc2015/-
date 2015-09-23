package com.cl.test.yjjc;

import com.cl.test.yjjc.MyLayout.RefreshListener;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity2 extends Activity {
	private MyLayout mLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main2);
		mLayout = (MyLayout) this.findViewById(R.id.mLayout);
		mLayout.setRefreshListener(new RefreshListener() {
			@Override
			public void onRefresh() {
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mLayout.stopRefresh();
							}
						});
					}
				}.start();
			}
		});
	}
	
}
