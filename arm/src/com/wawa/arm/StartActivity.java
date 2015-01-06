package com.wawa.arm;

import com.wawa.arm.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class StartActivity extends Activity {
	
	private ImageView screenImage;
	private Animation animation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.launch_screen);

		animation = new AlphaAnimation(1.0f, 0.1f);
		animation.setDuration(3000);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent();
				intent.setClass(StartActivity.this, ARMActivity.class);
				startActivity(intent);
				finish();
			}
		});
		screenImage = (ImageView) findViewById(R.id.main_launchimage);
		screenImage.startAnimation(animation);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
}