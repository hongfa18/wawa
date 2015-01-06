package com.wawa.arm.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.wawa.arm.ARMActivity;
import com.wawa.arm.net.WaitUIElement;

/**
 * 描述：
 *	
 * @author W.Y
 * @version 1.0
 * @created 2014年5月22日 上午11:18:57
 */
public class BaseFragment extends Fragment{
	private WaitUIElement dialog;
	protected Handler coomonHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case CommonConsts.REQUEST_START:
				dialog = new WaitUIElement(getActivity(),true);
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
    			Activity activity = getActivity();
    			Intent intent = new Intent(activity, ARMActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
				OMApplication.getInstance().logoutClearCacheData();
				activity.finish();
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
}
