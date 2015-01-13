package com.wawa.arm.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wawa.arm.ARMActivity;
import com.wawa.arm.R;
import com.wawa.arm.net.WaitUIElement;

/**
 * 描述：
 *	
 * @author W.Y
 * @version 1.0
 * @created 2014年5月22日 上午11:18:57
 */
public class BaseActivity extends FragmentActivity{
	// 是否允许全屏
	private boolean allowFullScreen = true;
	// 是否允许销毁
	private boolean allowDestroy = true;
	
	private TextView title;
	private ImageView logo;
	private ImageView backBtn;
	private LinearLayout layoutFirst;
	

	private View view;
	private WaitUIElement dialog;
	protected Handler coomonHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case CommonConsts.REQUEST_START:
				dialog = new WaitUIElement(BaseActivity.this,true);
    			dialog.showProcessDialog("");
				break;
			case CommonConsts.REQUEST_SUCCESS:
    			dialog.dismissProcessDialog();
    			dialog.showToast(arg0.obj.toString(), true);
				break;
			case CommonConsts.REQUEST_ERROR:
    			dialog.dismissProcessDialog();
    			dialog.showToast(arg0.obj.toString(), false);
				break;
			case CommonConsts.REQUEST_SESSION_VALIDATE:
    			dialog.dismissProcessDialog();
    			dialog.showToast(arg0.obj.toString(), false);
    			Intent intent = new Intent(BaseActivity.this, ARMActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				OMApplication.getInstance().logoutClearCacheData();
				finish();
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//自定义标题栏
		allowFullScreen = true;
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
	}
	protected void onCreate(Bundle savedInstanceState,boolean noTitle) {
		super.onCreate(savedInstanceState);
		if(noTitle)requestWindowFeature(Window.FEATURE_NO_TITLE);
		allowFullScreen = true;
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
	}
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		layoutFirst = (LinearLayout)findViewById(R.id.layout_first);
		title = (TextView)findViewById(R.id.title_text);
		logo = (ImageView)findViewById(R.id.title_icon);
		logo.setImageResource(R.drawable.ic_menu_set_as_clicked);
		layoutFirst.setVisibility(View.VISIBLE);
	}
	public void setContentView(int layoutResID,boolean noTitle) {
		super.setContentView(layoutResID);
		title = (TextView)findViewById(R.id.title_text);
	}
	public void setTitle(String titleStr){
		title.setText(titleStr);
	}
	public int getTitleVal(){
		int i = -1;
		if(title != null && title.getText() != null && !"".equals(title.getText().toString())){
			try {
				i = Integer.parseInt(title.getText().toString());
			} catch (Exception e) {
			}
		}
		return i;
	}
	/**
	 * 描述：
	 * 是否显示返回按钮
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年5月26日 下午4:33:39
	 * @param showBack
	 * @param clickListener
	 */
	public void showBackBtn(boolean showBack,View.OnClickListener clickListener){
		layoutFirst.setVisibility(View.GONE);
		logo.setImageResource(R.drawable.selector_back_bg);
		if(clickListener == null)
			clickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			};
		logo.setOnClickListener(clickListener);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	public boolean isAllowFullScreen() {
		return allowFullScreen;
	}

	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
