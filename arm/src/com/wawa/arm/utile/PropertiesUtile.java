package com.wawa.arm.utile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.utile.log.LogUtil;


public class PropertiesUtile {
	/*private static String TAG = OMApplication.getInstance().class.getSimpleName();
	
	private static final String PREFS_NAME = "com.zte.smartpaytest";
	private static SharedPreferences sharePrefer;
	private static Editor sharePreferEd;
	
	private static Map<String,Object> configs = new HashMap<String,Object>();
	private static Map<String,Object> cacheVal = new HashMap<String,Object>();
	
	public static void initPropertiesFromFile(Context context){
		Properties props = new Properties();
        InputStream in = null;
        try {
            in = context.getAssets().open(CommonConsts.PROPERTIES_NAME);
            props.load(in);
            String httpsUrl = props.getProperty(CommonConsts.HTTPS_BASE);
            configs.put(CommonConsts.HTTPS_BASE, httpsUrl);
            String requestScheme = props.getProperty(CommonConsts.REQUEST_SCHEME);
            configs.put(CommonConsts.REQUEST_SCHEME, requestScheme);
            Integer requestPort = Integer.valueOf(props.getProperty(CommonConsts.REQUEST_PORT));
            configs.put(CommonConsts.REQUEST_PORT, requestPort);
            Boolean needproxy = Boolean.valueOf(props.getProperty(CommonConsts.NEED_PROXY));
            configs.put(CommonConsts.NEED_PROXY, needproxy);
            String proxyUrl = props.getProperty(CommonConsts.PROXY_URL);
            configs.put(CommonConsts.PROXY_URL, proxyUrl);
            Integer proxyPort = Integer.valueOf(props.getProperty(CommonConsts.PROXY_PORT));
            configs.put(CommonConsts.PROXY_PORT, proxyPort);
            String proxyScheme = props.getProperty(CommonConsts.PROXY_SCHEME);
            configs.put(CommonConsts.PROXY_SCHEME, proxyScheme);
            int pageSize = Integer.valueOf(props.getProperty(CommonConsts.QUERY_PAGE_SIZE));
            configs.put(CommonConsts.QUERY_PAGE_SIZE, pageSize);
            String digestCode = props.getProperty(CommonConsts.DIGEST_CODE);
            configs.put(CommonConsts.DIGEST_CODE, digestCode);
            
            if (context != null){
            	sharePrefer = context.getSharedPreferences(PREFS_NAME, 0);
            	sharePreferEd = sharePrefer.edit();
            	setVal(CommonConsts.LAST_LOGIN_NAME, sharePrefer.getString(CommonConsts.LAST_LOGIN_NAME, ""), true);
            }
        }catch(Exception e){
        	LogUtil.e(TAG, "get properties error", e);
        }
	}
	
	*//**
	 * ������
	 * �洢���ݵ�����
	 * @author W.Y
	 * @version 1.0
	 * @created 2014��6��10�� ����5:04:13
	 * @param key
	 * @param val
	 *//*
	public static void setValToSharePrefer(String key,String val){
		if(sharePreferEd != null){
			sharePreferEd.putString(key, val);
			sharePreferEd.commit();
		}
		cacheVal.put(key, val);
	}
	*//**
	 * ������
	 *
	 * @author W.Y
	 * @version 1.0
	 * @created 2014��5��23�� ����7:59:40
	 * @param key  
	 * @param val
	 * @param isCacheVal �Ƿ񻺴�
	 *//*
	public static void setVal(String key,Object val,boolean isCacheVal){
		if(isCacheVal){
			cacheVal.put(key, val);
		}else{
			configs.put(key, val);
		}

	}
	
	@SuppressWarnings("unchecked")
	public static <T>  T getVal(String key,T defaultVal){
		T result = (T) configs.get(key);
		if(result == null){
			result = (T) cacheVal.get(key);
			if(result == null){
				if(key.equals(CommonConsts.LAST_LOGIN_NAME)){
					result = (T) sharePrefer.getString(CommonConsts.LAST_LOGIN_NAME, (String) defaultVal);
				}else{
					result = defaultVal;
				}
			}
		}
		return result;
	}
	
	public static void logoutClearCacheData(){
		cacheVal.clear();
	}*/
}
