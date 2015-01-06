package com.wawa.arm.utile.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.wawa.arm.R;
import com.wawa.arm.utile.log.LogUtil;

public class OMNetDialog extends Dialog{
	private Context context;
	private static OMNetDialog dialog;
	

	public OMNetDialog(Context context) {
		super(context);
		this.context = context;

	}
	public OMNetDialog(Context context, int theme) {
		super(context, theme);

	}

	public static OMNetDialog createDialog(Context context,int type,String msg) {
		if(dialog != null){
			dialog.dismiss();
			dialog = null;
		}
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_net_loading, null);
		dialog = new OMNetDialog(context,R.style.om_net_dialog_style);//使用AlertDialog时其中的listView里含的EditeText点击时不能弹出软键盘
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示dialog的头部
		dialog.setContentView(contentView);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		
		Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        Display d = dialogWindow.getWindowManager().getDefaultDisplay(); // 获取屏幕宽、高用
        
        //lp.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.6
        //lp.width = (int) (d.getWidth() * 0.46); // 宽度设置为屏幕的0.65
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        dialogWindow.setAttributes(lp);
        
        TextView tvMsg = (TextView)dialog.findViewById(R.id.net_loading_content);
        tvMsg.setVisibility(View.VISIBLE);
    	if (tvMsg != null){
    		if(msg != null && msg != ""){
    			tvMsg.setText(msg);
    		}else{
    			tvMsg.setVisibility(View.GONE);
    		}
    	}
    	ImageView infoOperatingIV = (ImageView)dialog.findViewById(R.id.head_img);
    	Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.net_loading_animation);
    	LinearInterpolator lin = new LinearInterpolator();
    	operatingAnim.setInterpolator(lin);
    	if (operatingAnim != null) {
    		infoOperatingIV.startAnimation(operatingAnim);
    	}
    	
    	/*dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
                {
                	return false;
                }else{
                	return true;
                }
            }
        });*/
		return dialog;
	}
	/*public boolean dispatchKeyEvent(KeyEvent event){
        switch(event.getKeyCode())
        {
        case KeyEvent.KEYCODE_BACK:
        	return false;
        default:
            break;
        }
        return super.dispatchKeyEvent(event);
    }*/
 
    public OMNetDialog setContent(String strMessage){
    	TextView tvMsg = (TextView)dialog.findViewById(R.id.net_loading_content);
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	return dialog;
    }
    
    public OMNetDialog changeImage(int resourceId){
    	ImageView img = (ImageView)dialog.findViewById(R.id.head_img);
    	if (img != null){
    		img.setImageResource(resourceId);;
    	}
    	return dialog;
    }
    
    public void dismiss() {
    	try {
    		if(dialog != null){
    			super.dismiss();
    			dialog = null;
    		}
    	} catch (Exception e) {
    		LogUtil.e(e);
    	}
    }
}
