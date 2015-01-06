package com.wawa.arm.service;

import java.net.URI;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.NetResult;
import com.wawa.arm.net.CustomerHttpClient;


/**
 * 描述：
 * SEC:安全中心
 * 登录密码修改、支付密码修改
 * @author W.Y
 * @version 1.0
 * @created 2014年5月23日 下午6:55:23
 */
public class SECService  extends BaseService{
	private static final String TAG = SECService.class.getSimpleName();
	
	public SECService(Handler coomonHandler){
		super(coomonHandler);
	}
	
	public void changePayPwd(Handler handler,Object... val){
		try {
			start(handler);
			URI uri = null;
			uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_CHANGE_PIN,null, null);
			JSONObject jb = new JSONObject();
			jb.put("oldPassword", val[0]);
			jb.put("newPassword", val[1]);
			NetResult result = CustomerHttpClient.put(uri,jb,8);
			if(result.isSuccess()){
				end(true, "", handler,null);
			}else{
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}

	public void changeLoginPwd(Handler handler,Object... val){
		try {
			start(handler);
			URI uri = null;
			uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_CHANGE_LOGIN_PWD,null, null);
			JSONObject jb = new JSONObject();
			jb.put("oldPassword", val[0]);
			jb.put("newPassword", val[1]);
			NetResult result = CustomerHttpClient.put(uri,jb,8);
			if(result.isSuccess()){
				end(true, "", handler,null);
			}else{
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}
}
