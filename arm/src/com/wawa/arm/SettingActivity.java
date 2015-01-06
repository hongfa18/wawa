package com.wawa.arm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wawa.arm.common.AppManager;
import com.wawa.arm.common.BaseActivity;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.User;
import com.wawa.arm.service.SYSService;
import com.wawa.arm.utile.MyToast;
import com.wawa.arm.utile.widgets.OMDialog;
import com.wawa.arm.utile.widgets.OMEditText;

public class SettingActivity extends BaseActivity{
	private TextView about;
	private Button save;
	private OMEditText armpl,armrm,armsx,armzq,appcs;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_setting);
        setTitle("设置");
        showBackBtn(true, null);
        
        save = (Button)findViewById(R.id.save);
        about = (TextView)findViewById(R.id.about);
        armpl = (OMEditText)findViewById(R.id.edit_arm_times);
        armrm = (OMEditText)findViewById(R.id.edit_arm_rm);
        armsx = (OMEditText)findViewById(R.id.edit_arm_sx);
        armzq = (OMEditText)findViewById(R.id.edit_arm_zq);
        appcs = (OMEditText)findViewById(R.id.edit_app_cs);
        armpl.setText(""+OMApplication.getInstance().getVal(CommonConsts.ARM_PL, 10000));
        armrm.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_RM, 11));
        armsx.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_SX, 2));
        armzq.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_ZQ, 1000));
        appcs.setText(""+OMApplication.getInstance().getVal(CommonConsts.APP_TIMEOUT, 10));
        
        
        save.setTextColor(getResources().getColor(R.color.white));
        save.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_next_ok));
		save.setClickable(true);
        save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_PL, armpl.getText().toString());
                OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_RM, armrm.getText().toString());
                OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_SX, armsx.getText().toString());
                OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_ZQ, armzq.getText().toString());
                OMApplication.getInstance().setValToSharePrefer(CommonConsts.APP_TIMEOUT, appcs.getText().toString());
                MyToast.showToast(SettingActivity.this,"保存成功", 3000);
        	}
        });
        about.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
				startActivity(intent);
        	}
        });
        
	}
	
}