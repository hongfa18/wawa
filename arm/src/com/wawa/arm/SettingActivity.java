package com.wawa.arm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.wawa.arm.common.BaseActivity;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.utile.MyToast;
import com.wawa.arm.utile.log.LogUtil;
import com.wawa.arm.utile.widgets.OMEditText;
import com.wawa.arm.utile.widgets.WiperSwitchView;

public class SettingActivity extends BaseActivity{
	private TextView about;
	private Button save;
	private OMEditText armpl,armrm,armsx,armzq,appcs,armpairname,armpairkey;
	private LinearLayout volChooseArea;
	private long time;
	private int count = 0;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_setting);
        setTitle("设置");
        showBackBtn(true, null);
        
        ((TextView)findViewById(R.id.for_code_open)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(count == 0){
					time = System.currentTimeMillis();
				}
				if((System.currentTimeMillis() - time) > 2000){
					time = 0;
					count = 0;
				}else if(count > 5){
					((TableLayout)findViewById(R.id.for_coder)).setVisibility(View.VISIBLE);
				}else{
					count++;
				}
				
			}
		});
        
        save = (Button)findViewById(R.id.save);
        about = (TextView)findViewById(R.id.about);
        about.setVisibility(View.VISIBLE);
        armpl = (OMEditText)findViewById(R.id.edit_arm_times);
        armrm = (OMEditText)findViewById(R.id.edit_arm_rm);
        armsx = (OMEditText)findViewById(R.id.edit_arm_sx);
        armzq = (OMEditText)findViewById(R.id.edit_arm_zq);
        appcs = (OMEditText)findViewById(R.id.edit_app_cs);
        armpairname = (OMEditText)findViewById(R.id.edit_app_default_pari_name);
        armpairkey = (OMEditText)findViewById(R.id.edit_app_default_pair_key);
        armpl.setText(""+OMApplication.getInstance().getVal(CommonConsts.ARM_PL, 10050+""));
        armrm.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_RM, 11+""));
        armsx.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_SX, 2+""));
        armzq.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_ZQ, 1000+""));
        appcs.setText(""+OMApplication.getInstance().getVal(CommonConsts.APP_TIMEOUT, 12+""));
        armpairname.setText(""+ OMApplication.getInstance().getVal(CommonConsts.ARM_PAIR_NAME, CommonConsts.DEFAULT_PERM_NAME));
        armpairkey.setText(""+OMApplication.getInstance().getVal(CommonConsts.ARM_PAIR_KEY, CommonConsts.DEFAULT_PERM_KEY));
        
        
        save.setTextColor(getResources().getColor(R.color.white));
        save.setBackgroundResource(R.drawable.selector_btn_next_ok);
		save.setClickable(true);
        save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(isOkVal(CommonConsts.ARM_PL, armpl.getText().toString()) &&
        				isOkVal(CommonConsts.ARM_RM, armrm.getText().toString()) &&
        				isOkVal(CommonConsts.ARM_SX, armsx.getText().toString()) &&
        				isOkVal(CommonConsts.ARM_ZQ, armzq.getText().toString()) &&
        				isOkVal(CommonConsts.APP_TIMEOUT, appcs.getText().toString())){
        			
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.APP_TIMEOUT, appcs.getText().toString());
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_PAIR_NAME, armpairname.getText().toString());
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_PAIR_KEY, armpairkey.getText().toString());
        				
        			if(OMApplication.getInstance().getVal(CommonConsts.ARM_PL, 10050+"").equals(armpl.getText().toString()) &&
        					OMApplication.getInstance().getVal(CommonConsts.ARM_RM, 11+"").equals(armrm.getText().toString()) &&
        					OMApplication.getInstance().getVal(CommonConsts.ARM_SX, 2+"").equals(armsx.getText().toString()) &&
        					OMApplication.getInstance().getVal(CommonConsts.ARM_ZQ, 1000+"").equals(armzq.getText().toString())){
        				//MyToast.showToast(SettingActivity.this,"无参数修改", 3000);
        				//return;
        				if(!OMApplication.getInstance().getVal(CommonConsts.APP_VOL_OPEN_STATU, "0").equals(isOpen) || 
        						(OMApplication.getInstance().getVal(CommonConsts.APP_VOL_OPEN_STATU, "0").equals("1") &&
            					!OMApplication.getInstance().getVal(CommonConsts.APP_VOL_VAL, "4").equals(currentChoose))){
        					OMApplication.getInstance().setValToSharePrefer(CommonConsts.APP_VOL_VAL,currentChoose);
                			OMApplication.getInstance().setValToSharePrefer(CommonConsts.APP_VOL_OPEN_STATU,isOpen);
        					MyToast.showToast(SettingActivity.this,"修改成功", 3000);
        				}
        				finish();
        				return;
        			}
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_PL, armpl.getText().toString());
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_RM, armrm.getText().toString());
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_SX, armsx.getText().toString());
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.ARM_ZQ, armzq.getText().toString());
        			
					OMApplication.getInstance().setValToSharePrefer(CommonConsts.APP_VOL_VAL,currentChoose);
        			OMApplication.getInstance().setValToSharePrefer(CommonConsts.APP_VOL_OPEN_STATU,isOpen);
        			
        			MyToast.showToast(SettingActivity.this,"修改成功", 3000);
        			Intent intent = new Intent();
                    intent.putExtra("needChangeVal", true);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
        		}
        	}
        });
        about.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right_my,R.anim.slide_out_left_my);
        	}
        });
        
        //实例化WiperSwitch  
        WiperSwitchView wiperSwitch = (WiperSwitchView)findViewById(R.id.wiperSwitch1);  
        radioBtn();
        volChooseArea = (LinearLayout)findViewById(R.id.layout_vol_choice_area);
        isOpen = OMApplication.getInstance().getVal(CommonConsts.APP_VOL_OPEN_STATU, "0");
        volChooseArea.setVisibility(View.GONE);
        if("0".equals(isOpen)){
        	//volChooseArea.setVisibility(View.GONE);
        	//设置初始状态为false  
        	wiperSwitch.setChecked(false);  
        }else{
        	//volChooseArea.setVisibility(View.VISIBLE);
        	wiperSwitch.setChecked(true); 
        }
        //设置监听  
        wiperSwitch.setOnChangedListener(new WiperSwitchView.OnChangedListener() {
			@Override
			public void OnChanged(WiperSwitchView wiperSwitch, boolean checkState) {
				if(checkState){
					isOpen = "1";
					//volChooseArea.setVisibility(View.VISIBLE);
				}else{
					isOpen = "0";
					//volChooseArea.setVisibility(View.GONE);
				}
			}
		});  

	}
	
	private String isOpen = "0";
	private String currentChoose;
	private void radioBtn(){
		RadioGroup maps = (RadioGroup)findViewById(R.id.chgCurrentVolType);
		final RadioButton low = (RadioButton)findViewById(R.id.vol_level_low);
		final RadioButton mid = (RadioButton)findViewById(R.id.vol_level_mid);
		final RadioButton high = (RadioButton)findViewById(R.id.vol_level_high);
		currentChoose = OMApplication.getInstance().getVal(CommonConsts.APP_VOL_VAL, "4");
		maps.clearCheck();
		switch (Integer.valueOf(currentChoose)) {
		case 2://低
			low.setChecked(true);
			break;
		case 4://中
			mid.setChecked(true);
			break;
		case 6://高
			high.setChecked(true);
			break;
		}
		maps.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == low.getId()){
					currentChoose = "2";
				}else if(checkedId == mid.getId()){
					currentChoose = "4";
				}else if(checkedId == high.getId()){
					currentChoose = "6";
				}
				
			}
		});
	}
	
	
	public boolean isOkVal(String type,String val){
		int intval = 0;
		boolean flag = false;
		try {
			intval = Integer.valueOf(val);
		} catch (Exception e) {
			MyToast.showToast(getApplicationContext(), "输入框输入有误，不能为空或非数字", 2000);
			LogUtil.e(e);
			return false;
		}
		if(CommonConsts.ARM_PL.equals(type)){//9400~9599,10050~10124
			if((9400 <= intval && intval <= 9599) || (10050 <= intval && intval <= 10124)){
				flag = true;
			}else{
				MyToast.showToast(getApplicationContext(), "监测频率输入错误，有效值9400~9599，10050~10124", 2000);
			}
		}else if(CommonConsts.ARM_RM.equals(type)){//1~128
			if(1 <= intval && intval <= 128){
				flag = true;
			}else{
				MyToast.showToast(getApplicationContext(), "扰码编号输入错误，有效值1~128", 2000);
			}
		}else if(CommonConsts.ARM_SX.equals(type)){//2~6
			if((2 <= intval && intval <= 6)){
				flag = true;
			}else{
				MyToast.showToast(getApplicationContext(), "时隙编号输入错误，有效值2~6", 2000);
			}
		}else if(CommonConsts.ARM_ZQ.equals(type)){//500/1000/2000/.../10000 单位毫秒
			if((500 <= intval && intval <= 10000) && intval%500 == 0){
				flag = true;
			}else{
				MyToast.showToast(getApplicationContext(), "上报周期输入错误，有效值500倍数，最低500，最大10000", 2000);
			}
		}else if(CommonConsts.APP_TIMEOUT.equals(type)){//10~100
			if((10 <= intval && intval <= 100)){
				flag = true;
			}else{
				MyToast.showToast(getApplicationContext(), "超时时间输入错误，有效值10~100", 2000);
			}
		}
		return flag;
	}
	
}