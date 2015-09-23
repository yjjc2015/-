package com.cl.test.yjjc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {
	private float mLastY;
	private boolean isRefreshable = false;
	
	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onUp() {
		}
		@Override
		public void onMove(int deltaY) {
		}
		@Override
		public void onDown() {
		}
	};

	public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyView(Context context) {
		this(context, null);
		initView(context);
	}
	
	private void initView(Context context) {
		this.setBackgroundColor(context.getResources().getColor(R.color.content));
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		float y = ev.getRawY();
		switch (action) {
			case MotionEvent.ACTION_DOWN:{
				isRefreshable = true;
				mOnRefreshListener.onDown();
				break;
			}
			case MotionEvent.ACTION_MOVE:{
				if (isRefreshable) {
					int deltaY = (int) ((y - mLastY));
					mOnRefreshListener.onMove(deltaY);
				}
				break;
			}
			case MotionEvent.ACTION_UP:{
				if (isRefreshable) {
					isRefreshable = false;
					mOnRefreshListener.onUp();
				}
				break;
			}
		}
		mLastY = y;
		return true;
	}

	public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
		this.mOnRefreshListener = mOnRefreshListener;
	}

	public interface OnRefreshListener {
		public void onDown();
		public void onUp();
		public void onMove(int deltaY);
	}

}
