package com.cl.test.yjjc;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MyLayout extends LinearLayout implements MyView.OnRefreshListener {
	private View mHeader;
	private TextView tvTime;
	private TextView tvTip;
	private ImageView ivArraw;
	private ProgressBar progress;
	private ObjectAnimator animToPull;
	private ObjectAnimator animToRelease;
	private int mHeaderDefaultHeight;
	public int mHeaderHeight;
	private DateFormat dateFormat;
	private static final String timeFormatStr = "yyyy年MM月dd日   hh:mm:ss";
	public static final int INCREMENT = -1;//每10ms height增加值
	public static final int PER_TIME = 10;//10ms
	private static final int START_PULL_DIVIATION = 50;//超过50像素，才会显示Header
	
	private MyView mContent;
	
	public int state = STATE_NORMAL;
	public static final int STATE_NORMAL = 0;
	public static final int STATE_READY = 1;
	public static final int STATE_REFRESHING = 2;
	
	public boolean mIsDown;//手指按下
	public Scroller mScroller;
	private RefreshListener mRefreshListener;

	public MyLayout(Context context) {
		this(context, null);
	}

	public MyLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeader(context);
		initContent(context);
		mScroller = new Scroller(context, new DecelerateInterpolator());
	}

	private void initContent(Context context) {
		mContent = new MyView(context);
		this.addView(mContent, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mContent.setOnRefreshListener(this);
	}

	private void initHeader(Context context) {
		mHeader = LayoutInflater.from(context).inflate(R.layout.layout_header,
				null);
		tvTip = (TextView) mHeader.findViewById(R.id.tip);
		tvTime = (TextView) mHeader.findViewById(R.id.time);
		ivArraw = (ImageView) mHeader.findViewById(R.id.arrow);
		progress = (ProgressBar) mHeader.findViewById(R.id.progress);
		measure(mHeader);
		mHeaderDefaultHeight = mHeader.getMeasuredHeight();
		animToRelease = ObjectAnimator.ofFloat(ivArraw, "rotation", 0f, 180f);
		animToRelease.setDuration(1000);
		animToPull = ObjectAnimator.ofFloat(ivArraw, "rotation", 180f, 0f);
		animToPull.setDuration(1000);
		this.addView(mHeader);
		setHeaderHeight(0);
	}

	private void measure(View view) {
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int height;
		if (lp.height > 0) {
			height = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	public void setHeaderHeight(int height) {
		mHeaderHeight = height;
		ViewGroup.LayoutParams lp = mHeader.getLayoutParams();
		lp.height = height;
		mHeader.setLayoutParams(lp);
	}
	
	private void updateState(int mState) {
		switch(mState){
			case STATE_NORMAL:
				state = STATE_NORMAL;
				animToPull.start();
				tvTip.setText("下拉可以刷新");
				break;
			case STATE_READY:
				state = STATE_READY;
				animToRelease.start();
				tvTip.setText("松开可以刷新");
				break;
			case STATE_REFRESHING:
				state = STATE_REFRESHING;
				ivArraw.setRotation(0);
				ivArraw.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				tvTip.setText("正在刷新...");
				break;
			default:
				break;
		}
	}
	
	private void smooth (int finalHeight) {
		mScroller.startScroll(0, mHeaderHeight, 0, finalHeight - mHeaderHeight, 1000);
		invalidate();
	}

	@Override
	public void onDown() {
		mIsDown = true;
		Date now = new Date();
		tvTime.setText(dateFormat.format(timeFormatStr, now));
	}

	@Override
	public void onUp() {
		if (mHeaderHeight <= 0) {
			if (mHeaderHeight < 0) {
				setHeaderHeight(0);
			}
			state = STATE_NORMAL;
			return;
		}
		
		mIsDown = false;
		if(state == STATE_READY) {
			updateState(STATE_REFRESHING);
			smooth(mHeaderDefaultHeight);
			mRefreshListener.onRefresh();
		} else {
			smooth(0);
		}
	}

	@Override
	public void onMove(int deltaY) {
		mHeaderHeight += deltaY;
		if (mHeaderHeight >= 0) {
			setHeaderHeight(mHeaderHeight);
			if (mHeaderHeight <=  mHeaderDefaultHeight) {
				if (state != STATE_NORMAL) {
					updateState(STATE_NORMAL);
				} 
			} else {
				if (state != STATE_READY) {
					updateState(STATE_READY);
				}
			}
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mIsDown) {
				mScroller.abortAnimation();
				return;
			}
			setHeaderHeight(mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	public void stopRefresh() {
		ivArraw.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		state = STATE_NORMAL;
		smooth(0);
	}
	
	public void setRefreshListener (RefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
	}
	
	interface RefreshListener {
		void onRefresh();
	}
}
