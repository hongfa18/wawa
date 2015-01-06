package com.wawa.arm;

import android.os.Bundle;
import android.widget.TextView;

import com.wawa.arm.common.BaseActivity;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.utile.MobileUtils;

/**
 * 关于
* @ClassName: AboutActivity 
* @Description: TODO
* @author W.Y 
* @date 2014年11月16日 上午2:07:34 
*
 */
public class AboutActivity extends BaseActivity{
	private TextView sn,appver,name,mac,armver,armno;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_about);
        setTitle("关于");
        showBackBtn(true, null);
        
        
        sn = (TextView) findViewById(R.id.phone_sn);
        appver = (TextView) findViewById(R.id.app_version);
        name = (TextView) findViewById(R.id.arm_name);
        mac = (TextView) findViewById(R.id.arm_mac);
        armno = (TextView) findViewById(R.id.arm_no);
        armver = (TextView) findViewById(R.id.arm_version);
        initData();
	}
	
	private void initData(){
		String sn = OMApplication.getInstance().getVal(CommonConsts.APP_SN, null);
		String ver = OMApplication.getInstance().getVal(CommonConsts.APP_VER, null);
		if(sn == null) sn = MobileUtils.getTelInfo()[7];
		if(sn == null){
			sn = "手机SN：空";
		}else{
			OMApplication.getInstance().setVal(CommonConsts.APP_SN, sn,true);
			sn = "手机SN："+sn;
		}
		if(ver == null) ver = MobileUtils.getAppVersionName(this.getBaseContext());
		if(ver == null){
			ver = "APP版本：空";
		}else{
			OMApplication.getInstance().setVal(CommonConsts.APP_VER, ver,true);
			ver = "APP版本："+ver;
		}
		this.sn.setText(sn);
		appver.setText(ver);
		armno.setText("设备IMEI："+OMApplication.getInstance().getVal(CommonConsts.ARM_NO, "空"));
		armver.setText("设备版本："+OMApplication.getInstance().getVal(CommonConsts.ARM_VERSION, "空"));
		name.setText("设备名称："+OMApplication.getInstance().getVal(CommonConsts.ARM_NAME, "空"));
		mac.setText("设备地址："+OMApplication.getInstance().getVal(CommonConsts.ARM_ADDR, "空"));
	}

}
