package com.wawa.arm.service;

import java.net.URI;

import org.apache.http.client.utils.URIUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.NetResult;
import com.wawa.arm.net.CustomerHttpClient;
import com.wawa.arm.utile.StringUtils;

public abstract class BaseService {
	private Handler coomonHandler;
	protected String requestScheme = OMApplication.getInstance().getVal(CommonConsts.REQUEST_SCHEME, "https");
	protected String baseUrl = OMApplication.getInstance().getVal(CommonConsts.HTTPS_BASE, "www.baidu.com");
	protected int port = OMApplication.getInstance().getVal(CommonConsts.REQUEST_PORT, 80);
	
	protected BaseService(Handler coomonHandler){
		this.coomonHandler = coomonHandler;
	}
	
	public void sendSMSCode(Handler handler,Object... val){
		try {
			start(handler);
			URI uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_SMSCODE,null, null);
			NetResult result = CustomerHttpClient.put(uri,null,4);
			if(result.isSuccess()){
				end(true, "", handler,null);
			}else{
				//end(false, result.getErrorTipStr(), handler,null);
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e("--******--BaseService--******--", "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}
	
	protected void start(Handler handler){
		coomonHandler.sendEmptyMessage(CommonConsts.REQUEST_START);
		if(handler != null){
			handler.sendEmptyMessage(CommonConsts.REQUEST_START);
		}
	}
	/**
	 * 描述：
	 *
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年5月27日 下午3:03:59
	 * @param isSuccess 操作是否成功
	 * @param tip 操作结果提示语句
	 * @param handler 业务关联UI的handler
	 * @param data 传递到业务关联UI的网络返回数据--另外可以通过OMApplication.getInstance().setVal操作
	 */
	protected void end(boolean isSuccess,Object tip,Handler handler,Object data){
		Message msg = new Message();
		msg.what = isSuccess?CommonConsts.REQUEST_SUCCESS:CommonConsts.REQUEST_ERROR;
		msg.obj = tip;
		coomonHandler.sendMessage(msg);
		if(handler != null){
			Message msg1 = new Message();
			msg1.what = isSuccess?CommonConsts.REQUEST_SUCCESS:CommonConsts.REQUEST_ERROR;
			msg1.obj = data;
			handler.sendMessage(msg1);
		}
	}
	protected void end(boolean isSuccess,Object tip,Handler handler,Object data,String errorCode){
		Message msg = new Message();
		if(!StringUtils.isEmpty(errorCode) && errorCode.equalsIgnoreCase("CM0000000004")){
			msg.what = CommonConsts.REQUEST_SESSION_VALIDATE;
    	}else{
    		msg.what = isSuccess?CommonConsts.REQUEST_SUCCESS:CommonConsts.REQUEST_ERROR;
    	}
		msg.obj = tip;
		coomonHandler.sendMessage(msg);
		if(handler != null){
			Message msg1 = new Message();
			msg1.what = isSuccess?CommonConsts.REQUEST_SUCCESS:CommonConsts.REQUEST_ERROR;
			msg1.obj = data;
			handler.sendMessage(msg1);
		}
	}
}
