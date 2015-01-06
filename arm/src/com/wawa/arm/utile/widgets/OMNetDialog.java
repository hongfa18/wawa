package com.wawa.arm.utile.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
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
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_net_loading, null);
		dialog = new OMNetDialog(context,R.style.om_net_dialog_style);//ʹ��AlertDialogʱ���е�listView�ﺬ��EditeText���ʱ���ܵ��������
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//����ʾdialog��ͷ��
		dialog.setContentView(contentView);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		
		Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        Display d = dialogWindow.getWindowManager().getDefaultDisplay(); // ��ȡ��Ļ������
        
        //lp.height = (int) (d.getHeight() * 0.9); // �߶�����Ϊ��Ļ��0.6
        //lp.width = (int) (d.getWidth() * 0.46); // �������Ϊ��Ļ��0.65
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        dialogWindow.setAttributes(lp);
        
        TextView tvMsg = (TextView)dialog.findViewById(R.id.net_loading_content);
        tvMsg.setVisibility(View.GONE);
    	/*if (tvMsg != null){
    		if(!StringUtils.isEmpty(msg)){
    			tvMsg.setText(msg);
    		}else{
    			tvMsg.setVisibility(View.GONE);
    		}
    	}*/
    	ImageView infoOperatingIV = (ImageView)dialog.findViewById(R.id.head_img);
    	Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.net_loading_animation);
    	LinearInterpolator lin = new LinearInterpolator();
    	operatingAnim.setInterpolator(lin);
    	if (operatingAnim != null) {
    		infoOperatingIV.startAnimation(operatingAnim);
    	}
		return dialog;
	}
 
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
