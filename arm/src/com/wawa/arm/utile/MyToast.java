package com.wawa.arm.utile;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wawa.arm.R;


/**
 * 描述：自定义Toast 防止Toast多个重叠显示
 *
 * @author W.Y
 * @version 1.0
 * @created 2014年5月20日 下午3:21:29
 */
public class MyToast {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };
    
    private static Toast diyToast;
    private static Handler diyHandler = new Handler();
    private static Runnable diy = new Runnable() {
        public void run() {
        	diyToast.cancel();
        }
    };

    
    public static void showToast(Context mContext, String text, int duration) {
       
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(r, duration);

        mToast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }
    
    public static void showDIYToast(Activity mContext, String text, int duration,boolean isSuccess){
       //LayoutInflater inflater = mContext.getLayoutInflater();
	   
	   diyHandler.removeCallbacks(diy);
       if (diyToast != null){
    	   //diyToast.setText(text);
    	   ((ImageView) diyToast.getView().findViewById(R.id.tip_head_img)).setImageResource(isSuccess?R.drawable.infomsg_icon_success:R.drawable.infomsg_icon_error);
    	   ((TextView)diyToast.getView().findViewById(R.id.tip_content)).setText(text);
       }else{
    	   //diyToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
    	   View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_toast_diy,null);
    	   ImageView image = (ImageView) layout.findViewById(R.id.tip_head_img);
    	   image.setImageResource(isSuccess?R.drawable.infomsg_icon_success:R.drawable.infomsg_icon_error);
    	   TextView contentText = (TextView) layout.findViewById(R.id.tip_content);
    	   contentText.setText(text);
    	   diyToast = new Toast(mContext);
    	   diyToast.setGravity(Gravity.CENTER,0,0);
    	   diyToast.setDuration(Toast.LENGTH_LONG);
    	   diyToast.setView(layout);
       }
       diyHandler.postDelayed(diy, duration);

       diyToast.show();
    }
}

