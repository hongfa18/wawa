package com.wawa.arm;

import android.os.Bundle;
import android.widget.TextView;

import com.wawa.arm.common.BaseActivity;

/**
 * ����
* @ClassName: AboutActivity 
* @Description: TODO
* @author W.Y 
* @date 2014��11��16�� ����2:07:34 
*
 */
public class AboutActivity extends BaseActivity{
	private TextView sn,appver,name,mac,armver;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_about);
        setTitle("����");
        showBackBtn(true, null);
        
        
        sn = (TextView) findViewById(R.id.phone_sn);
        appver = (TextView) findViewById(R.id.app_version);
        name = (TextView) findViewById(R.id.arm_name);
        mac = (TextView) findViewById(R.id.arm_mac);
        armver = (TextView) findViewById(R.id.arm_version);
	}

}
