package com.cl.test.yjjc;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class MyHandler extends Handler {
	private final WeakReference<MyLayout> mLayout;
	public MyHandler (MyLayout layout){
		mLayout = new WeakReference<MyLayout>(layout);
	}
	@Override
	public void handleMessage(Message msg) {
		MyLayout layout = mLayout.get();
		if (layout != null) {
			int height = msg.what;
			layout.setHeaderHeight(height);
		}
	}
}
