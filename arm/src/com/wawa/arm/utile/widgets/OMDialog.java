package com.wawa.arm.utile.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.Spanned;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wawa.arm.R;


public class OMDialog extends Dialog{
	private Context context;
	private static OMDialog dialog;
	
	public static final int DIALOG_MODE_ONE = 1;
	public static final int DIALOG_MODE_TWO = 2;
	private int currentMode = 1;

	public OMDialog(Context context) {
		super(context);
		this.context = context;

	}
	public OMDialog(Context context, int theme) {
		super(context, theme);

	}

	public static OMDialog createDialog(Context context) {
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_send_val_code, null);
		dialog = new OMDialog(context,R.style.klt_confirm_dialog_style);//ʹ��AlertDialogʱ���е�listView�ﺬ��EditeText���ʱ���ܵ��������
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//����ʾdialog��ͷ��
		dialog.setContentView(contentView);
		dialog.setCanceledOnTouchOutside(false);
		
		Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        Display d = dialogWindow.getWindowManager().getDefaultDisplay(); // ��ȡ��Ļ������
        
//        lp.height = (int) (d.getHeight() * 0.9); // �߶�����Ϊ��Ļ��0.6
        lp.width = (int) (d.getWidth() * 0.90); // �������Ϊ��Ļ��0.65
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//        dialogWindow.setAttributes(lp);
		Button btnCancel = (Button) contentView
				.findViewById(R.id.login_send_val_code_btn_cancel);
		TextView tvMsg = (TextView)dialog.findViewById(R.id.login_send_val_code_content);
    	tvMsg.setVisibility(View.GONE);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				dialog = null;
			}

		});
		return dialog;
	}
	
 
    public OMDialog setTitile(String strTitle){
    	TextView tvMsg = (TextView)dialog.findViewById(R.id.tv_title_dialog_lock_mode_setting);
    	if (tvMsg != null){
    		tvMsg.setText(strTitle);
    	}
    	return dialog;
    }
    
    public OMDialog setContent(Spanned strMessage){
    	TextView tvMsg = (TextView)dialog.findViewById(R.id.login_send_val_code_content);
    	tvMsg.setVisibility(View.VISIBLE);
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	return dialog;
    }
    
    /**
     * ������
     * ����dialog�ײ���ťģʽ
     * 1����OK��Cancel��ť
     * 2��ֻ��OK��ť
     * @author W.Y
     * @version 1.0
     * @created 2014��6��20�� ����9:26:12
     * @param mode
     */
    public void setDialogMode(int mode){
    	LinearLayout one = (LinearLayout)dialog.findViewById(R.id.dialog_mode_one);
    	LinearLayout two = (LinearLayout)dialog.findViewById(R.id.dialog_mode_two);
    	switch (mode) {
		case DIALOG_MODE_ONE:
			one.setVisibility(View.VISIBLE);
			two.setVisibility(View.GONE);
			currentMode = DIALOG_MODE_ONE;
			break;
		case DIALOG_MODE_TWO:
			one.setVisibility(View.GONE);
			two.setVisibility(View.VISIBLE);
			currentMode = DIALOG_MODE_TWO;
			break;
		default:
			break;
		}
    }
    
    public OMDialog setBtnOkClickListener(View.OnClickListener clickListener){
    	Button btnOk = null;
    	if(currentMode == DIALOG_MODE_ONE){
    		btnOk = (Button)dialog.findViewById(R.id.login_send_val_code_btn_ok);
    	}
    	if(currentMode == DIALOG_MODE_TWO){
    		btnOk = (Button)dialog.findViewById(R.id.dialog_mode_two_btn_ok);
    	}
    	if (btnOk != null){
    		btnOk.setOnClickListener(clickListener);
    	}
    	return dialog;
    }
    public OMDialog setBtnCancelClickListener(View.OnClickListener clickListener){
    	if(currentMode == DIALOG_MODE_TWO){
    		throw new NumberFormatException("current dialog mode is DIALOG_MODE_TWO,can't set cancel clicklistener."); 
    	}
    	Button btnCancel = (Button)dialog.findViewById(R.id.login_send_val_code_btn_cancel);
    	if (btnCancel != null){
    		btnCancel.setOnClickListener(clickListener);
    	}
    	return dialog;
    }
}
