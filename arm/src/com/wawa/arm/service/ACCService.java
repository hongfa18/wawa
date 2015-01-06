package com.wawa.arm.service;

import java.net.URI;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.NetResult;
import com.wawa.arm.entity.QueryDataObject;
import com.wawa.arm.entity.TradeRecord;
import com.wawa.arm.net.CustomerHttpClient;
import com.wawa.arm.utile.JSONUtile;
import com.wawa.arm.utile.PropertiesUtile;
import com.wawa.arm.utile.StringUtils;


/**
 * 描述：
 *ACC：账号管理
 *余额查询、近三次交易、一年交易
 * @author W.Y
 * @version 1.0
 * @created 2014年5月23日 下午6:51:28
 */
public class ACCService  extends BaseService{
	private static final String TAG = ACCService.class.getSimpleName();
	
	public ACCService(Handler coomonHandler) {
		super(coomonHandler);
	}

	public void queryBalance(Handler handler, Object... val) {
		try {
			start(handler);
			//User user = OMApplication.getInstance().getVal(CommonConsts.LOGIN_USER, null);
			//if(user != null && user.getLoginPwd() != null && user.getLoginPwd().equals(val[0])){
			URI uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_BANLANCE,null, null);
			NetResult result = CustomerHttpClient.put(uri,null,5);
			if(result.isSuccess()){
				String balance = JSONUtile.parseBalanceToMomery(result.getResultJsonStr());
				if(StringUtils.isEmpty(balance)){
					end(false, OMApplication.BALANCE_TIP_002, handler,null);
				}else{
					end(true, "", handler,null);
				}
			}else{
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}

	public void queryMiniStatement(Handler handler, Object... val) {
		try {
			start(handler);
			//User user = OMApplication.getInstance().getVal(CommonConsts.LOGIN_USER, null);
			//if(user != null && user.getLoginPwd() != null && user.getLoginPwd().equals(val[0])){
			URI uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_MINI_STATEMENT,null, null);
			NetResult result = CustomerHttpClient.put(uri,null,6);
			if(result.isSuccess()){
				QueryDataObject<TradeRecord> data = JSONUtile.parseQueryMiniTradeToMomery(result.getResultJsonStr());
				if(data == null){
					end(false,OMApplication.STATEMENT_TIP_002, handler,null);
				}else{
					end(true, "", handler,null);
				}
			}else{
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
		
	}

	public void queryYearStatement(Handler handler,  Object... val) {
		try {
			start(handler);
			//User user = OMApplication.getInstance().getVal(CommonConsts.LOGIN_USER, null);
			//if(user != null && user.getLoginPwd() != null && user.getLoginPwd().equals(val[0])){
				URI uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_YEAR_STATEMENT,null, null);
				JSONObject jb = new JSONObject();
				jb.put("beginDate", val[0]);
				jb.put("endDate", val[1]);
				jb.put("beginPos", val[2]);
				jb.put("pageSize",OMApplication.getInstance().getVal(CommonConsts.QUERY_PAGE_SIZE, 30));
				NetResult result = CustomerHttpClient.put(uri,jb,7);
				if(result.isSuccess()){
					QueryDataObject<TradeRecord> data = JSONUtile.parseQueryYearTradeToMomery(result.getResultJsonStr(),val[0],val[1]);
					if(data == null){
						end(false, OMApplication.STATEMENT_TIP_002, handler,null);
					}else{
						end(true, "", handler,null);
					}
				}else{
					end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
				}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}

	public void logOut(Handler handler, Object... val) {
		try {
			start(handler);
			URI uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_LOGOUT,null, null);
			NetResult result = CustomerHttpClient.put(uri,null,3);
			//if(result.isSuccess()){
			end(true, OMApplication.LOGOUT_TIP_001, handler,null);//不管是否成功都提示成功并跳转到登录界面
			/*}else{
				end(false, result.getErrorTipStr(), handler,null);
			}*/
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
		
	}
	
	public void logOutFormExit() {
		try {
			URI uri = URIUtils.createURI(requestScheme,baseUrl , port, CommonConsts.REQUEST_URI_LOGOUT,null, null);
			CustomerHttpClient.put(uri,null,3);
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
        }
	}
}
