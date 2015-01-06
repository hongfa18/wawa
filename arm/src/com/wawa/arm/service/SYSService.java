package com.wawa.arm.service;

import java.net.URI;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.AppParamMap;
import com.wawa.arm.entity.NetResult;
import com.wawa.arm.entity.User;
import com.wawa.arm.net.CustomerHttpClient;
import com.wawa.arm.utile.JSONUtile;
import com.wawa.arm.utile.PropertiesUtile;

/**
 * 描述：
 * SYS:系统管理
 * 登录
 * 签退
 * 参数获取
 * 手机验证码
 * @author W.Y
 * @version 1.0
 * @created 2014年5月23日 下午6:47:16
 */
public class SYSService extends BaseService{
	private static final String TAG = SYSService.class.getSimpleName();
	
	public SYSService(Handler coomonHandler){
		super(coomonHandler);
	}
	public void login(Handler handler,Object... val){
		try {
			start(handler);
			//List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
			URI uri = null;
			//uri = URIUtils.createURI(CommonConsts.HTTPS_SCHEME, OMApplication.getInstance().getVal(CommonConsts.HTTPS_BASE, "www.baidu.com"), -1, CommonConsts.REQUEST_URI_LOGIN,URLEncodedUtils.format(formparams, "UTF-8"), null);
			uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_LOGIN,null, null);
			JSONObject jb = new JSONObject();
			jb.put("userCode", val[0]);
			jb.put("password", val[1]);
			NetResult result = CustomerHttpClient.put(uri,jb,1);
			if(result.isSuccess()){
				User user = JSONUtile.parseUserInfoToMomery(result.getResultJsonStr());
				if(user != null){
					uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_APP_PARAM,null, null);
					NetResult result2 = CustomerHttpClient.put(uri,null,2);
					if(result2.isSuccess()){
						AppParamMap paramMap = JSONUtile.parseParamMapToMomery(result2.getResultJsonStr());
						if(paramMap != null){
							user.setUserCode((String)val[0]);
							user.setLoginPwd((String)val[1]);
							OMApplication.getInstance().setVal(CommonConsts.LOGIN_USER, user,true);
							end(true, "", handler,null);
						}else{
							end(false, OMApplication.LOGIN_TIP_002, handler,null);
						}
					}else{
						end(false, result2.getErrorTipStr(), handler,null,result.getErrorCode());
					}
				}else{
					end(false, result.getErrorTipStr(), handler,null);
				}
			}else{
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}
}
