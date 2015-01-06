package com.wawa.arm;

import android.os.Bundle;
import android.widget.TextView;

import com.wawa.arm.common.BaseActivity;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.utile.MobileUtils;

/**
 * ����
* @ClassName: AboutActivity 
* @Description: TODO
* @author W.Y 
* @date 2014��11��16�� ����2:07:34 
*
 */
public class AboutActivity extends BaseActivity{
	private TextView sn,appver,name,mac,armver,armno;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_about);
        setTitle("����");
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
			sn = "�ֻ�SN����";
		}else{
			OMApplication.getInstance().setVal(CommonConsts.APP_SN, sn,true);
			sn = "�ֻ�SN��"+sn;
		}
		if(ver == null) ver = MobileUtils.getAppVersionName(this.getBaseContext());
		if(ver == null){
			ver = "APP�汾����";
		}else{
			OMApplication.getInstance().setVal(CommonConsts.APP_VER, ver,true);
			ver = "APP�汾��"+ver;
		}
		this.sn.setText(sn);
		appver.setText(ver);
		armno.setText("�豸IMEI��"+OMApplication.getInstance().getVal(CommonConsts.ARM_NO, "��"));
		armver.setText("�豸�汾��"+OMApplication.getInstance().getVal(CommonConsts.ARM_VERSION, "��"));
		name.setText("�豸���ƣ�"+OMApplication.getInstance().getVal(CommonConsts.ARM_NAME, "��"));
		mac.setText("�豸��ַ��"+OMApplication.getInstance().getVal(CommonConsts.ARM_ADDR, "��"));
	}

}
